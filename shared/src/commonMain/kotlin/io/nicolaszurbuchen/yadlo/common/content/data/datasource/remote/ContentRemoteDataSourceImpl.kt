package io.nicolaszurbuchen.yadlo.common.content.data.datasource.remote

import io.nicolaszurbuchen.yadlo.common.content.data.datasource.remote.api.ContentApi
import io.nicolaszurbuchen.yadlo.common.content.data.datasource.remote.dto.ContentDocumentDto
import io.nicolaszurbuchen.yadlo.common.error.AppError
import io.nicolaszurbuchen.yadlo.common.error.AppException
import kotlinx.coroutines.CancellationException

class ContentRemoteDataSourceImpl(
    private val api: ContentApi,
) : ContentRemoteDataSource {
    override suspend fun fetchDocument(
        path: String,
        etag: String?,
    ): ContentDocumentDto? =
        try {
            api.fetchDocument(path, etag)
        } catch (e: CancellationException) {
            throw e
        } catch (e: AppException) {
            // The api already classified this one — a 404 on an edition path is a different
            // problem from no signal, and collapsing both into Unavailable would lose that.
            throw e
        } catch (_: Exception) {
            throw AppException(AppError.Network.Unavailable)
        }
}
