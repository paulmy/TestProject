package com.clean.arch.mvvm.data.repositories

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.clean.arch.mvvm.data.entities.Movie
import com.clean.arch.mvvm.net.MovieApi
import java.lang.Exception

class MovieDataSource(private val apiService : MovieApi) : PagingSource<Int, Movie>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        return try {
            val pageNum = params.key ?: 1
            val response = apiService.getPopularMoviesPage(pageNum)
            LoadResult.Page(
                data = response.movies,
                prevKey = if (pageNum < 1) null else pageNum - 1,
                nextKey = if (pageNum > response.totalPages) null else pageNum + 1
            )
        } catch (e : Exception) {
            Log.e("Response error", e.message!!)
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}