package org.devfleet.zkillboard.zkilla.eve.esi;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

interface ESIApi {
    /**
     * Get type information
     * Get information on a type  ---  This route expires daily at 11:05
     * @param typeId An Eve item type ID (required)
     * @param datasource The server name you would like data from (optional, default to tranquility)
     * @param language Language to use in the response, takes precedence over Accept-Language (optional, default to en-us)
     * @return Call&lt;GetUniverseTypesTypeIdOk&gt;
     */

    @GET("v3/universe/types/{type_id}/")
    Call<ESIType> getUniverseType(
            @Path("type_id") Long typeId,
            @Query("language") String language,
            @Query("datasource") String datasource);

    /**
     * Get names and categories for a set of ID&#39;s
     * Resolve a set of IDs to names and categories. Supported ID&#39;s for resolving are: Characters, Corporations, Alliances, Stations, Solar Systems, Constellations, Regions, Types.  ---
     * @param ids The ids to resolve (required)
     * @param datasource The server name you would like data from (optional, default to tranquility)
     * @return Call&lt;List<PostUniverseNames200Ok>&gt;
     */

    @POST("v2/universe/names/")
    Call<List<ESIName>> postUniverseNames(
            @Body List<Long> ids,
            @Query("datasource") String datasource);

}
