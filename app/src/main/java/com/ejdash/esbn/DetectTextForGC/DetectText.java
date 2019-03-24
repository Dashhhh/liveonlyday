package com.ejdash.esbn.DetectTextForGC;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ejdash.esbn.R;
import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.ImageSource;
import com.google.protobuf.ByteString;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DetectText extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_cloud_example);

        try {
            detectText("/sdcard/DCIM/Camera/testimage.jpeg");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /*
        사진에서 글자를 찾아서 리턴함
     */
    private static void detectText(String filePath) throws Exception {
        List<AnnotateImageRequest> requests = new ArrayList<>();

        Image image = getImage(filePath);

        Feature feature = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
        AnnotateImageRequest request = AnnotateImageRequest.newBuilder().addFeatures(feature).setImage(image).build();
        requests.add(request);

        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
            BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();

            for (AnnotateImageResponse res : responses) {
                if (res.hasError()) {
                    System.out.println("Error: " + res.getError().getMessage());
                    return;
                }

                // For full list of available annotations, see http://g.co/cloud/vision/docs
                for (EntityAnnotation annotation : res.getTextAnnotationsList()) {
                    System.out.println("Text: " + annotation.getDescription());
                    System.out.println("Position : " + annotation.getBoundingPoly());
                }
            }
        }
    }

    /*
        Local 혹은 Google Cloud Storage에서 이미지를 가져와 Image Class로 Return해줌
     */
    private static Image getImage(String filePath) throws IOException {
        Image image;

        if (filePath.startsWith("gs://")) { // GCS에서 이미지를 가져올때 image 생성
            ImageSource imgSource = ImageSource.newBuilder().setGcsImageUri(filePath).build();
            image = Image.newBuilder().setSource(imgSource).build();
        }
        else { // 로컬에서 이미지를 가져올때 image 생성
            ByteString imgBytes = ByteString.readFrom(new FileInputStream(filePath));
            image = Image.newBuilder().setContent(imgBytes).build();
        }

        return image;
    }

}
