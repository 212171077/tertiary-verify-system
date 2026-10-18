import { ErrorResponse } from "../model/ErrorResponse";

export class CommonUtil {
  static buildErrorResponse(error: any): ErrorResponse {
    try {
      const errorResponse = new ErrorResponse();
      errorResponse.responseCode = error.error.responseCode;
      errorResponse.responseMessage = error.error.responseMessage;

      errorResponse.errors = new Map<string, string>(
        Object.entries(error.error.errors)
      );
      return errorResponse;
    } catch (error) {
      return null;
    }
  }
}
