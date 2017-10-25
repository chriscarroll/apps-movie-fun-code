package org.superbiz.moviefun.blobstore;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import org.apache.tika.Tika;
import org.apache.tika.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Optional;

public class S3Store implements BlobStore {

    private final AmazonS3 s3client;
    private final String bucketName;
    private final Tika tika = new Tika();

    public S3Store(AmazonS3 s3client, String bucketName) {
        this.s3client = s3client;
        this.bucketName = bucketName;
    }

    @Override
    public void put(Blob blob) throws IOException {
        s3client.putObject(bucketName, blob.name, blob.inputStream, new ObjectMetadata());
    }

    @Override
    public Optional<Blob> get(String name) throws IOException {
        if (!s3client.doesObjectExist(bucketName, name)) {
            return Optional.empty();
        }

        try (S3Object s3Object = s3client.getObject(bucketName, name)) {
            S3ObjectInputStream content = s3Object.getObjectContent();

            byte[] bytes = IOUtils.toByteArray(content);

            return Optional.of(new Blob(
                    name,
                    new ByteArrayInputStream(bytes),
                    tika.detect(bytes)
            ));
        }
    }
}
