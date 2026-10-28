export const CATEGORIES = [
    'FOOD',
    'TRANSPORT',
    'HOUSING',
    'HEALTH',
    'LEISURE',
    'EDUCATION',
    'OTHER',
] as const;

export type Category = (typeof CATEGORIES)[number];

export interface Expense {
    id: number;
    description: string;
    amount: number;
    date: string;
    category: Category;
    createdAt: string;
}

export interface ExpenseRequest {
    description: string;
    amount: number;
    date: string;
    category?: Category;
}