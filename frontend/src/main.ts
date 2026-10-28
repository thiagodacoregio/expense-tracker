import './style.css';
import { ApiError, createExpense, fetchExpenses } from './api';
import { CATEGORIES, type Category, type Expense, type ExpenseRequest } from './types';

const app = document.querySelector<HTMLDivElement>('#app')!;

app.innerHTML = `
  <h1>Expense Tracker</h1>

  <form id="expense-form">
    <label>
      Description
      <input name="description" required maxlength="255" />
      <small class="field-error" data-error-for="description"></small>
    </label>

    <label>
      Amount (£)
      <input name="amount" type="number" step="0.01" min="0.01" required />
      <small class="field-error" data-error-for="amount"></small>
    </label>

    <label>
      Date
      <input name="date" type="date" required />
      <small class="field-error" data-error-for="date"></small>
    </label>

    <label>
      Category
      <select name="category">
        <option value="">Let the app decide</option>
        ${CATEGORIES.map((category) => `<option value="${category}">${category}</option>`).join('')}
      </select>
    </label>

    <button type="submit">Add expense</button>
    <p id="form-message"></p>
  </form>

  <h2>Expenses</h2>
  <ul id="expense-list"></ul>
`;

const list = document.querySelector<HTMLUListElement>('#expense-list')!;
const form = document.querySelector<HTMLFormElement>('#expense-form')!;
const formMessage = document.querySelector<HTMLParagraphElement>('#form-message')!;
const submitButton = form.querySelector<HTMLButtonElement>('button')!;
const dateInput = form.elements.namedItem('date') as HTMLInputElement;

function todayAsIsoDate(): string {
    const now = new Date();
    const offsetInMs = now.getTimezoneOffset() * 60_000;
    return new Date(now.getTime() - offsetInMs).toISOString().slice(0, 10);
}

function resetDateInput(): void {
    dateInput.value = todayAsIsoDate();
    dateInput.max = todayAsIsoDate();
}

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
        renderExpenses(await fetchExpenses());
    } catch (error) {
        list.innerHTML = '<li>Could not load expenses. Is the API running?</li>';
        console.error(error);
    }
}

function clearErrors(): void {
    form.querySelectorAll<HTMLElement>('.field-error').forEach((element) => {
        element.textContent = '';
    });
    formMessage.textContent = '';
}

function showFieldErrors(fieldErrors: Record<string, string>): void {
    for (const [field, message] of Object.entries(fieldErrors)) {
        const element = form.querySelector<HTMLElement>(`[data-error-for="${field}"]`);
        if (element) {
            element.textContent = message;
        }
    }
}

function readForm(): ExpenseRequest {
    const data = new FormData(form);
    const category = data.get('category') as string;

    return {
        description: data.get('description') as string,
        amount: Number(data.get('amount')),
        date: data.get('date') as string,
        category: category ? (category as Category) : undefined,
    };
}

form.addEventListener('submit', async (event) => {
    event.preventDefault();
    clearErrors();
    submitButton.disabled = true;

    try {
        await createExpense(readForm());
        form.reset();
        resetDateInput();
        formMessage.textContent = 'Expense added!';
        await loadExpenses();
    } catch (error) {
        if (error instanceof ApiError) {
            showFieldErrors(error.fieldErrors);
            formMessage.textContent = error.message;
        } else {
            formMessage.textContent = 'Something went wrong. Please try again.';
            console.error(error);
        }
    } finally {
        submitButton.disabled = false;
    }
});

resetDateInput();
loadExpenses();