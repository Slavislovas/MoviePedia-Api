package com.api.MoviePedia.util;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ImageComparator {
    public static boolean areImagesEqual(byte[] imageBytes1, byte[] imageBytes2) {
        try {
            // Create instances of MessageDigest for MD5 hashing
            MessageDigest md5Digest = MessageDigest.getInstance("MD5");

            // Calculate MD5 hash for the first image
            byte[] hash1 = calculateHash(md5Digest, imageBytes1);

            // Calculate MD5 hash for the second image
            byte[] hash2 = calculateHash(md5Digest, imageBytes2);

            // Compare the hashes
            return MessageDigest.isEqual(hash1, hash2);
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
            return false; // Handle exceptions as needed
        }
    }

    private static byte[] calculateHash(MessageDigest digest, byte[] imageData) throws IOException {
        digest.update(imageData);
        return digest.digest();
    }
}
