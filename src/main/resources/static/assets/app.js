const apiBase = '';

const elements = {
  creatorName: document.getElementById('creator-name'),
  apiKey: document.getElementById('api-key'),
  createCreator: document.getElementById('create-creator'),
  saveApiKey: document.getElementById('save-api-key'),
  walletKey: document.getElementById('wallet-key'),
  walletReturn: document.getElementById('wallet-return'),
  walletFail: document.getElementById('wallet-fail'),
  saveWallet: document.getElementById('save-wallet'),
  botName: document.getElementById('bot-name'),
  botToken: document.getElementById('bot-token'),
  registerBot: document.getElementById('register-bot'),
  botList: document.getElementById('bot-list'),
  selectedBot: document.getElementById('selected-bot'),
  chatBotId: document.getElementById('chat-bot-id'),
  chatTelegramId: document.getElementById('chat-telegram-id'),
  chatTitle: document.getElementById('chat-title'),
  chatType: document.getElementById('chat-type'),
  registerChat: document.getElementById('register-chat'),
  chatList: document.getElementById('chat-list'),
  productChatId: document.getElementById('product-chat-id'),
  productName: document.getElementById('product-name'),
  productPrice: document.getElementById('product-price'),
  productDuration: document.getElementById('product-duration'),
  productRecurring: document.getElementById('product-recurring'),
  createProduct: document.getElementById('create-product'),
  productList: document.getElementById('product-list'),
  invoiceProductId: document.getElementById('invoice-product-id'),
  invoiceBotId: document.getElementById('invoice-bot-id'),
  invoiceUser: document.getElementById('invoice-user'),
  createInvoice: document.getElementById('create-invoice'),
  createWalletOrder: document.getElementById('create-wallet-order'),
  orderList: document.getElementById('order-list'),
  loadDashboard: document.getElementById('load-dashboard'),
  dashboardSummary: document.getElementById('dashboard-summary')
};

function showToast(message) {
  M.toast({ html: message, classes: 'indigo' });
}

function getApiKey() {
  return localStorage.getItem('telegramPayApiKey') || '';
}

function setApiKey(key) {
  localStorage.setItem('telegramPayApiKey', key);
  elements.apiKey.value = key;
  M.updateTextFields();
}

async function apiRequest(path, options = {}) {
  const headers = options.headers || {};
  const apiKey = getApiKey();
  if (apiKey) {
    headers['X-Api-Key'] = apiKey;
  }
  if (!headers['Content-Type'] && options.body) {
    headers['Content-Type'] = 'application/json';
  }
  const response = await fetch(apiBase + path, { ...options, headers });
  if (!response.ok) {
    const text = await response.text();
    throw new Error(text || 'Request failed');
  }
  if (response.status === 204) {
    return null;
  }
  return response.json();
}

function renderList(container, items, formatter) {
  container.innerHTML = '';
  if (!items || items.length === 0) {
    container.innerHTML = '<li class="collection-item">No data yet</li>';
    return;
  }
  items.forEach(item => {
    const li = document.createElement('li');
    li.className = 'collection-item';
    li.innerHTML = formatter(item);
    container.appendChild(li);
  });
}

async function loadBots() {
  const bots = await apiRequest('/api/bots');
  renderList(elements.botList, bots, bot => `
    <div class=\"bot-item\" data-bot-id=\"${bot.id}\">
      <strong>#${bot.id} ${bot.name}</strong><br/>
      <small>Webhook secret: ${bot.webhookSecretToken}</small><br/>
      <a class=\"btn-flat indigo-text select-bot\" data-bot-id=\"${bot.id}\">Select</a>
    </div>
  `);
  elements.botList.querySelectorAll('.select-bot').forEach(button => {
    button.addEventListener('click', () => {
      const botId = button.getAttribute('data-bot-id');
      elements.chatBotId.value = botId;
      elements.invoiceBotId.value = botId;
      elements.selectedBot.textContent = `Selected bot #${botId}`;
      M.updateTextFields();
      loadChats().catch(console.warn);
    });
  });
}

async function loadChats() {
  const chats = await apiRequest('/api/chats');
  const selectedBotId = Number(elements.chatBotId.value);
  const filtered = Number.isFinite(selectedBotId) ? chats.filter(chat => chat.botInstanceId === selectedBotId) : chats;
  renderList(elements.chatList, filtered, chat => `#${chat.id} ${chat.title}<br/><small>${chat.telegramChatId} (${chat.type})</small>`);
}

async function loadProducts() {
  const products = await apiRequest('/api/products');
  renderList(elements.productList, products, product => `#${product.id} ${product.name} - ${product.priceStars} XTR`);
}

async function loadOrders() {
  const orders = await apiRequest('/api/orders');
  renderList(elements.orderList, orders, order => `#${order.id} ${order.status} (${order.provider}) - ${order.amountStars} XTR`);
}

elements.createCreator.addEventListener('click', async () => {
  const name = elements.creatorName.value.trim();
  if (!name) {
    showToast('Enter creator name');
    return;
  }
  const response = await apiRequest('/api/creators', {
    method: 'POST',
    body: JSON.stringify({ name })
  });
  setApiKey(response.apiKey);
  showToast('Creator created');
});

elements.saveApiKey.addEventListener('click', () => {
  const key = elements.apiKey.value.trim();
  setApiKey(key);
  showToast('API key saved');
});

elements.saveWallet.addEventListener('click', async () => {
  const payload = {
    storeApiKey: elements.walletKey.value.trim(),
    returnUrl: elements.walletReturn.value.trim(),
    failReturnUrl: elements.walletFail.value.trim()
  };
  await apiRequest('/api/walletpay/config', {
    method: 'POST',
    body: JSON.stringify(payload)
  });
  showToast('Wallet Pay config saved');
});

elements.registerBot.addEventListener('click', async () => {
  const payload = {
    name: elements.botName.value.trim(),
    botToken: elements.botToken.value.trim()
  };
  const bot = await apiRequest('/api/bots', {
    method: 'POST',
    body: JSON.stringify(payload)
  });
  elements.botName.value = '';
  elements.botToken.value = '';
  M.updateTextFields();
  showToast(`Bot registered (#${bot.id})`);
  await loadBots();
});

elements.registerChat.addEventListener('click', async () => {
  const payload = {
    botInstanceId: Number(elements.chatBotId.value),
    telegramChatId: elements.chatTelegramId.value.trim(),
    title: elements.chatTitle.value.trim(),
    type: elements.chatType.value.trim()
  };
  await apiRequest('/api/chats', {
    method: 'POST',
    body: JSON.stringify(payload)
  });
  showToast('Chat connected');
  await loadChats();
});

elements.createProduct.addEventListener('click', async () => {
  const payload = {
    chatId: Number(elements.productChatId.value),
    name: elements.productName.value.trim(),
    priceStars: Number(elements.productPrice.value),
    durationDays: elements.productDuration.value ? Number(elements.productDuration.value) : null,
    recurringMonthly: elements.productRecurring.checked
  };
  await apiRequest('/api/products', {
    method: 'POST',
    body: JSON.stringify(payload)
  });
  showToast('Product created');
  await loadProducts();
});

elements.createInvoice.addEventListener('click', async () => {
  const payload = {
    productId: Number(elements.invoiceProductId.value),
    botInstanceId: Number(elements.invoiceBotId.value),
    telegramUserId: elements.invoiceUser.value.trim()
  };
  await apiRequest('/api/orders/stars', {
    method: 'POST',
    body: JSON.stringify(payload)
  });
  showToast('Invoice sent');
  await loadOrders();
});

elements.createWalletOrder.addEventListener('click', async () => {
  const payload = {
    productId: Number(elements.invoiceProductId.value),
    telegramUserId: elements.invoiceUser.value.trim()
  };
  await apiRequest('/api/orders/walletpay', {
    method: 'POST',
    body: JSON.stringify(payload)
  });
  showToast('Wallet Pay order created');
  await loadOrders();
});

elements.loadDashboard.addEventListener('click', async () => {
  const summary = await apiRequest('/api/dashboard');
  elements.dashboardSummary.innerHTML = `
    <ul>
      <li>Total orders: ${summary.totalOrders}</li>
      <li>Paid orders: ${summary.totalPaid}</li>
      <li>Active subscribers: ${summary.activeSubscribers}</li>
      <li>Revenue (Stars): ${summary.totalRevenueStars}</li>
    </ul>
  `;
});

window.addEventListener('load', async () => {
  const storedKey = getApiKey();
  if (storedKey) {
    elements.apiKey.value = storedKey;
  }
  M.updateTextFields();
  if (storedKey) {
    try {
      await Promise.all([loadBots(), loadChats(), loadProducts(), loadOrders()]);
    } catch (error) {
      console.warn(error);
    }
  }
});
