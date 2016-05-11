package name.caiyao.microreader.api.itHome;

import name.caiyao.microreader.bean.itHome.ItHomeArticle;
import name.caiyao.microreader.bean.itHome.ItHomeResponse;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by 蔡小木 on 2016/3/24 0024.
 */
public interface ItHomeApi {

    @GET("/xml/newslist/news.xml")
    Observable<ItHomeResponse> getItHomeNews();

    @GET("/xml/newslist/news_{minNewsId}.xml")
    Observable<ItHomeResponse> getMoreItHomeNews(@Path("minNewsId") String minNewsId);

    @GET("/xml/newscontent/{id}.xml")
    Observable<ItHomeArticle> getItHomeArticle(@Path("id") String id);

}
