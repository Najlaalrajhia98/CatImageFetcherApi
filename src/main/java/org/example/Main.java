package org.example;

public class Main {
    public static void main(String[] args) {
        CatImageFetcher fetcher = new CatImageFetcher();
        String filename = fetcher.fetchCatImage();
        System.out.println("Cat image saved to " + filename);
    }
}
