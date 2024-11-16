public class ServiceResponse<T> {
    boolean isSuccessful;
    T data;

    String statusCode;

    ServiceResponse(boolean isSuccessful, T data, String statusCode) {
        this.isSuccessful = isSuccessful;
        this.data = data;
        this.statusCode = statusCode;
    }

    /**
     * The request has succeeded.
     *
     * @param data internal data
     * @param <T>  class of the data(usually HashMap<String, String>)
     * @return a new object of ServiceResponse with the statusCode "200 OK".
     */
    public static <T> ServiceResponse<T> OK(T data) {
        return new ServiceResponse<T>(true, data, "200 OK");
    }

    /**
     * The server encountered an unexpected condition.
     *
     * @param data internal data
     * @param <T>  class of the data(usually HashMap<String, String>)
     * @return a new object of ServiceResponse with the statusCode "500 Internal Server Error".
     */
    public static <T> ServiceResponse<T> InternalServerError(T data) {
        return new ServiceResponse<>(false, data, "500 Internal Server Error");
    }

    /**
     * The server does not support the request method.
     *
     * @param data internal data
     * @param <T>  class of the data(usually HashMap<String, String>)
     * @return a new object of ServiceResponse with the statusCode "400 Bad Request".
     */
    public static <T> ServiceResponse<T> BadRequest(T data) {
        return new ServiceResponse<>(false, data, "400 Bad Request");
    }
}
