export interface Audit {
  id: string;
  username: string;
  method: string;
  path: string;
  params?: string;
  createdAt: number;
}

export type AuditQuery = {
  username?: string;
  method?: string;
  path?: string;
};
