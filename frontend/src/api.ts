import type { Expense } from './types';

const API_URL = import.meta.env.VITE_API_URL ?? 'http://localhost:8080';

export async function fetchExpenses(): Promise<Expense[]> {
    const response = await fetch(`${API_URL}/api/expenses`);

    if (!response.ok) {
        throw new Error(`Failed to load expenses: ${response.status}`);
    }

    return response.json();
}