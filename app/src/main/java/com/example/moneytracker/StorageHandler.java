package com.example.moneytracker;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

public class StorageHandler {
    private static StorageHandler instance;
    private final FirebaseStorage storage = FirebaseStorage.getInstance();

    public interface ImageCallback {
        void onImageDownloaded(Bitmap image);
    }

    private StorageHandler() {
    }

    public static void init() {
        if (instance == null) {
            instance = new StorageHandler();
        }
    }

    public static StorageHandler getInstance() {
        if (instance == null) {
            throw new IllegalStateException("StorageHandler must be initialized first by calling init() method.");
        }
        return instance;
    }

    public void downloadFile(String baseRef, String filePath, ImageCallback callback) {
        StorageReference imageReference = storage.getReference().child(baseRef).child(filePath);
        final long ONE_MEGABYTE = 1024 * 1024;
        imageReference.getBytes(ONE_MEGABYTE)
                .addOnSuccessListener(bytes -> handleSuccess(bytes, callback))
                .addOnFailureListener(this::handleFailure);
    }

    private void handleSuccess(byte[] bytes, ImageCallback callback) {
        Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        callback.onImageDownloaded(image);
    }

    private void handleFailure(Exception exception) {
        MySignal.getInstance().toast(exception.getMessage());
    }


    public Task<Uri> uploadFile(Uri fileUri, String baseRef, String filePath) {
        StorageReference imageReference = storage.getReference().child(baseRef).child(filePath);
        return imageReference.putFile(fileUri)
                .addOnFailureListener(e -> MySignal.getInstance().toast(e.getMessage()))
                .continueWithTask(task -> handleUploadTask(task, imageReference));
    }

    private Task<Uri> handleUploadTask(Task<UploadTask.TaskSnapshot> task, StorageReference imageReference) throws Exception {
        if (!task.isSuccessful()) {
            throw Objects.requireNonNull(task.getException());
        }
        return imageReference.getDownloadUrl();
    }





    public Task<Void> deleteImage(String baseRef, String filePath) {
        StorageReference imageReference = storage.getReference().child(baseRef).child(filePath);
        return imageReference.delete();
    }
}
