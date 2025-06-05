package sleppynavigators.studyupbackend.infrastructure.medium;

import java.net.URL;

/**
 * Client interface for managing media storage operations.
 */
public interface MediumStorageClient {

    /**
     * Generates a signed URL for uploading media files. User can upload media file using <code>PUT</code> request to
     * the returned URL.
     *
     * @param userId   the ID of the user uploading the media
     * @param filename the name of the file to be uploaded
     * @return a signed URL for uploading the media file
     */
    URL getUploadUrl(Long userId, String filename);

    /**
     * Updates the tag information of an existing media file.
     *
     * @param mediaUrl the URL of the media file to update
     * @param tagging  new tag information for the media. format: <code>key=value</code>
     */
    void updateMediaTag(URL mediaUrl, String tagging);

    /**
     * Checks if the media URL is managed by this storage client.
     *
     * @param mediaUrl the URL of the media file to check
     * @return <code>true</code> if the media URL is managed by this client, <code>false</code> otherwise
     */
    boolean isManagedByUs(URL mediaUrl);
}
