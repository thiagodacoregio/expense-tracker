import type { Expense, ExpenseRequest } from './types';

const API_URL = import.meta.env.VITE_API_URL ?? 'http://localhost:8080';

export class ApiError extends Error {
    readonly fieldErrors: Record<string, string>;

    constructor(message: string, fieldErrors: Record<string, string> = {}) {
        super(message);
        this.name = 'ApiError';
        this.fieldErrors = fieldErrors;
    }
}

interface ProblemDetail {
    title?: string;
    detail?: string;
    errors?: Record<string, string>;
}

async function toApiError(response: Response): Promise<ApiError> {
    try {
        const problem: ProblemDetail = await response.json();
        return new ApiError(
            problem.detail ?? problem.title ?? `Request failed: ${response.status}`,
            problem.errors ?? {},
        );
    } catch {
        return new ApiError(`Request failed: ${response.status}`);
    }
}

export async function fetchExpenses(): Promise<Expense[]> {
    const response = await fetch(`${API_URL}/api/expenses`);

    if (!response.ok) {
        throw await toApiError(response);
    }

    return response.json();
}

export async function createExpense(request: ExpenseRequest): Promise<Expense> {
    const response = await fetch(`${API_URL}/api/expenses`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(request),
    });

    if (!response.ok) {
        throw await toApiError(response);
    }

    return response.json();
}
