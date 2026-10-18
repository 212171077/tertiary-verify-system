
export class LoginResponse {
    token: string;
    type: string;
    id: number;
    username: string;
    email: string;
    roles: string[];
    changePassword: boolean;
  }