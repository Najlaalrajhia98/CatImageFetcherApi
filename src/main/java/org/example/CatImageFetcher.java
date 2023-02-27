package org.example;

import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CatImageFetcher {
    private static final String API_URL = "https://api.thecatapi.com/v1/images/search";
    private static final String DATA_DIR = "data";

    private OkHttpClient client = new OkHttpClient();
    private Gson gson = new Gson();
    private List<String> savedImageHashes = new ArrayList<>();

    /**
     * @fetchCatImage This method is used to fetch a cat image from the API endpoint. It first builds the API URL and creates a request object with the API URL
     * @return
     */
    public String fetchCatImage() {
        try {
            // build the API URL with the necessary parameters
            String url = API_URL;

            // create a request object with the API URL
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            // send the request using the OkHttp client
            Response response = client.newCall(request).execute();

            // check if the response was successful
            if (response.isSuccessful()) {
                // parse the JSON response into a Java object using GSON
                String responseBody = response.body().string();
                CatImage catImage = gson.fromJson(responseBody, CatImage[].class)[0];

                // generate a hash value for the image
                String imageHash = Integer.toString(catImage.getUrl().hashCode());

                // check if the image has already been saved
                if (savedImageHashes.contains(imageHash)) {
                    System.out.println("This image has already been saved!");
                    return imageHash + ".jpg";
                }

                // create the data directory if it doesn't exist
                Path dataDirPath = Paths.get(DATA_DIR);
                if (!Files.exists(dataDirPath)) {
                    Files.createDirectory(dataDirPath);
                }

                // generate a unique filename for the image
                String filename = UUID.randomUUID().toString() + "_" + imageHash + ".jpg";
                Path filePath = dataDirPath.resolve(filename);

                // download the image and save it to a file
                RequestBody requestBody = RequestBody.create(catImage.getUrl(), null);
                Response imageResponse = client.newCall(new Request.Builder().url(catImage.getUrl()).build()).execute();
                FileOutputStream fos = new FileOutputStream(filePath.toString());
                fos.write(imageResponse.body().bytes());
                fos.close();

                // add the hash value of the saved image to the list of saved image hashes
                savedImageHashes.add(imageHash);

                // return the filename of the saved image
                return filename;
            } else {
                System.out.println("Error: Unable to fetch cat image.");
                return "";
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            return "";
        }
    }
}
