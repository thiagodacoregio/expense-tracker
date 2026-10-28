export type Category =
    | 'FOOD'
    | 'TRANSPORT'
    | 'HOUSING'
    | 'HEALTH'
    | 'LEISURE'
    | 'EDUCATION'
    | 'OTHER';

export interface Expense {
    id: number;
    description: string;
    amount: number;
    date: string;
    category: Category;
    createdAt: string;
}