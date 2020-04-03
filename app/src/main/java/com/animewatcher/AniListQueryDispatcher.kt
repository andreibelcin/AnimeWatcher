package com.animewatcher

import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Operation
import com.apollographql.apollo.api.Query
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import okhttp3.OkHttpClient
import okhttp3.Request


class AniListQueryDispatcher(accessToken: String? = null) {
    companion object {
        private val TAG = this::class.qualifiedName
        private val ANILIST_GRAPHQL_ENDPOINT = "https://graphql.anilist.co"
    }

    private val apolloClient: ApolloClient

    init {
        val httpClient = OkHttpClient.Builder()
            .addInterceptor {
                val original: Request = it.request()
                val builder: Request.Builder =
                    original.newBuilder().method(original.method(), original.body())
                builder.header("Authorization", "Bearer $accessToken")
                it.proceed(builder.build())
            }
            .build()
        val apolloClientBuilder = ApolloClient.builder()
        if (accessToken != null) {
            apolloClientBuilder.okHttpClient(httpClient)
        }
        this.apolloClient = apolloClientBuilder
            .serverUrl(ANILIST_GRAPHQL_ENDPOINT)
            .build()
    }

    fun <D : Operation.Data, T, V : Operation.Variables> dispatch(
        query: Query<D, T, V>,
        responseHandler: (Response<T>) -> Unit,
        errorHandler: (ApolloException) -> Unit
    ) {
        apolloClient.query(query).enqueue(object : ApolloCall.Callback<T>() {
            override fun onFailure(e: ApolloException) = errorHandler(e)

            override fun onResponse(response: Response<T>) = responseHandler(response)

        })
    }
}