package bg.sofia.uni.fmi.mjt.client;
import bg.sofia.uni.fmi.mjt.client.exceptions.query.InvalidCountryException;
import bg.sofia.uni.fmi.mjt.client.exceptions.query.InvalidPageException;
import bg.sofia.uni.fmi.mjt.client.exceptions.query.InvalidPageSizeException;
import bg.sofia.uni.fmi.mjt.client.exceptions.query.InvalidQueryException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class QueryParameters {
    //map is read only won't use concurrent
    private static final Map<String, String> COUNTRIES = new HashMap<>();
    private static final String COUNTRY_PARAM = "country=";
    private static final String CATEGORY_PARAM = "category=";
    private static final String KEYWORDS_PARAM = "q=";
    private static final String PAGE_SIZE_PARAM = "pageSize=";
    private static final String PAGE_PARAM = "page=";
    private static final int DEFAULT_PAGE_SIZE_API = 20;
    private static final int MIN_ARTICLES_PER_PAGE = 1;
    private static final int MAX_ARTICLES_PER_PAGE = 100;
    private static final int FIRST_PAGE = 1;

    private List<String> keywords;
    //optional
    private String country;
    private String category;
    private Integer pageSize;
    private Integer page;

    public List<String> getKeywords() {
        return keywords;
    }
    public String getCategory() {
        return category;
    }
    public String getCountry() {
        return country;
    }
    public int getPageSize() {
        return pageSize;
    }
    public String getAsUriField() {
        StringBuilder result = new StringBuilder();
        result.append(KEYWORDS_PARAM);
        var iterator = keywords.iterator();
        while (iterator.hasNext()) {
            result.append(iterator.next());
            if (iterator.hasNext()) {
                result.append("+");
            }
        }
        if (country != null) {
            result.append("&");
            result.append(COUNTRY_PARAM);
            result.append(country);
        }
        if (category != null) {
            result.append("&");
            result.append(CATEGORY_PARAM);
            result.append(category);
        }
        if (pageSize != null) {
            result.append("&");
            result.append(PAGE_SIZE_PARAM);
            result.append(pageSize);
        }
        if (page != null) {
            result.append("&");
            result.append(PAGE_PARAM);
            result.append(page);
        }
        return result.toString();
    }
    public static QueryParametersBuilder builder(List<String> keywords) throws InvalidQueryException {
        if (keywords == null || keywords.isEmpty() || keywords.stream().allMatch(x -> x.isBlank())) {
            throw new InvalidQueryException("at least one word must be passed");
        }
        return new QueryParametersBuilder(keywords);
    }
    void nextPage() {
        this.page++;
    }
    private QueryParameters(QueryParametersBuilder builder) {
        this.keywords = builder.keywords;
        this.category = builder.category;
        this.country = builder.country;
        this.pageSize = builder.pageSize;
        this.page = builder.page;
    }

    private static String getCountryCode(String countryName) throws InvalidCountryException {
        var countriesToCodes = COUNTRIES.entrySet();
        for (var country : countriesToCodes) {
            if (country.getKey().equalsIgnoreCase(countryName)) {
                return country.getValue();
            }
        }
        throw new InvalidCountryException("invalid country name passed");
    }

    public static class QueryParametersBuilder {
        private List<String> keywords;
        private String country;
        private String category;
        private Integer pageSize;
        private Integer page;
        private QueryParametersBuilder(List<String> keywords) {
            this.keywords = keywords;
            this.pageSize = DEFAULT_PAGE_SIZE_API;
            this.page = FIRST_PAGE;
        }
        public QueryParametersBuilder setCountry(String country) throws InvalidCountryException  {
            if (country == null || country.isEmpty() || country.isBlank()) {
                throw new IllegalArgumentException("nul, empty or blank passed");
            }
            this.country = getCountryCode(country);
            return this;
        }
        public QueryParametersBuilder setCategory(Category category) {
            if (category == null) {
                throw new IllegalArgumentException("null passed");
            }
            this.category = category.name().toLowerCase();
            return this;
        }

        // next 2 methods are no modifier, they are only to be used within methods of NewsFeedClient
        QueryParametersBuilder setPageSize(int pageSize) throws InvalidPageSizeException {
            if ( pageSize < MIN_ARTICLES_PER_PAGE || pageSize > MAX_ARTICLES_PER_PAGE) {
                throw new InvalidPageSizeException("pages can contain between 1 and 100 articles");
            }
            this.pageSize = pageSize;
            return this;
        }
        QueryParametersBuilder setPage(int page) throws InvalidPageException {
            if (page < 1) {
                throw new InvalidPageException("pages start from 1 and must be less than ");
            }
            this.page = page;
            return this;
        }
        public QueryParameters build() {
            return new QueryParameters(this);
        }
    }

    static {
        COUNTRIES.put("United Arab Emirates", "ae");
        COUNTRIES.put("Argentina", "ar");
        COUNTRIES.put("Austria", "at");
        COUNTRIES.put("Australia", "au");
        COUNTRIES.put("Belgium", "be");
        COUNTRIES.put("Bulgaria", "bg");
        COUNTRIES.put("Brazil", "br");
        COUNTRIES.put("Canada", "ca");
        COUNTRIES.put("Switzerland", "ch");
        COUNTRIES.put("China", "cn");
        COUNTRIES.put("Colombia", "co");
        COUNTRIES.put("Cuba", "cu");
        COUNTRIES.put("Czech Republic", "cz");
        COUNTRIES.put("Germany", "de");
        COUNTRIES.put("Egypt", "eg");
        COUNTRIES.put("France", "fr");
        COUNTRIES.put("United Kingdom", "gb");
        COUNTRIES.put("Greece", "gr");
        COUNTRIES.put("Hong Kong", "hk");
        COUNTRIES.put("Hungary", "hu");
        COUNTRIES.put("Indonesia", "id");
        COUNTRIES.put("Ireland", "ir");
        COUNTRIES.put("Israel", "il");
        COUNTRIES.put("India", "in");
        COUNTRIES.put("Japan", "jp");
        COUNTRIES.put("Korea", "kr");
        COUNTRIES.put("Lithuania", "lt");
        COUNTRIES.put("Latvia", "lv");
        COUNTRIES.put("Morocco", "ma");
        COUNTRIES.put("Mexico", "mx");
        COUNTRIES.put("Malaysia", "my");
        COUNTRIES.put("Nigeria", "ng");
        COUNTRIES.put("Netherlands", "nl");
        COUNTRIES.put("Norway", "no");
        COUNTRIES.put("Philippines", "ph");
        COUNTRIES.put("Poland", "pl");
        COUNTRIES.put("Portugal", "pt");
        COUNTRIES.put("Romania", "ro");
        COUNTRIES.put("Serbia", "rs");
        COUNTRIES.put("Russian Federation", "ru");
        COUNTRIES.put("Saudi Arabia", "sa");
        COUNTRIES.put("Sweden", "se");
        COUNTRIES.put("Singapore", "sg");
        COUNTRIES.put("Slovenia", "si");
        COUNTRIES.put("Slovakia", "sk");
        COUNTRIES.put("Thailand", "th");
        COUNTRIES.put("Turkey", "tr");
        COUNTRIES.put("Taiwan", "tw");
        COUNTRIES.put("Ukraine", "uk");
        COUNTRIES.put("United States", "us");
        COUNTRIES.put("Venezuela", "ve");
        COUNTRIES.put("South Africa", "za");
    }
}
