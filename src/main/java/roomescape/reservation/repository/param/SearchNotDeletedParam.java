package roomescape.reservation.repository.param;

public class SearchNotDeletedParam<T> implements SearchParam {

    private final boolean isFirst;
    private final String paramName;
    private final T paramValue;

    public SearchNotDeletedParam(boolean isFirst, String paramName, T paramValue) {
        this.isFirst = isFirst;
        this.paramName = paramName;
        this.paramValue = paramValue;
    }

    @Override
    public String addParamToQuery(String query) {
        return setStartQuery(isFirst, query) + paramName + " <> '" + paramValue + "'";
    }
}
