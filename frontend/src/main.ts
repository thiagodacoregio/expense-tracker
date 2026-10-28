import './style.css';
import { fetchExpenses } from './api';
import type { Expense } from './types';

const app = document.querySelector<HTMLDivElement>('#app')!;

app.innerHTML = `
  <h1>Expense Tracker</h1>
  <ul id="expense-list"></ul>
`;

const list = document.querySelector<HTMLUListElement>('#expense-list')!;

function renderExpenses(expenses: Expense[]): void {
    if (expenses.length === 0) {
        list.innerHTML = '<li>No expenses yet.</li>';
        return;
    }

    const items = expenses.map((expense) => {
        const item = document.createElement('li');
        item.textContent =
            `${expense.date} · ${expense.description} · £${expense.amount.toFixed(2)} · ${expense.category}`;
        return item;
    });

    list.replaceChildren(...items);
}

async function loadExpenses(): Promise<void> {
    try {
        const expenses = await fetchExpenses();
        renderExpenses(expenses);
    } catch (error) {
        list.innerHTML = '<li>Could not load expenses. Is the API running?</li>';
        console.error(error);
    }
}

loadExpenses();