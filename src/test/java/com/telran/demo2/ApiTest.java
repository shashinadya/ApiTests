package com.telran.demo2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.telran.demo2.entity.Book;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ApiTest {
    private static final String GET_BOOKS = "http://localhost:8080/books";
    private static final String GET_BOOK_BY_ID = "http://localhost:8080/books/{id}";
    private static final String NAME_OF_HEADER_CONTEN_TTYPE = "Content-Type";
    private static final String VALUE_OF_HEADER_CONTENT_TYPE = "application/json";
    private ObjectMapper mapper = new ObjectMapper();
    private HttpClient client = HttpClient.newBuilder().build();

    @BeforeAll
    static void runBookApi() {
        BookApi.main(new String[]{});
    }

    private HttpResponse<String> createNewBook(Book book) throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .header(NAME_OF_HEADER_CONTEN_TTYPE, VALUE_OF_HEADER_CONTENT_TYPE)
                .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(book)))
                .uri(URI.create(GET_BOOKS))
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @Test
    @Order(1)
    public void getAllBooksWhenBookListIsEmptyTest() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(GET_BOOKS)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("[]", response.body());
    }

    @Test
    @Order(3)
    public void postNewBookTest() throws IOException, InterruptedException {
        Book book = new Book();
        book.setAuthor("S.King");
        book.setName("Green mile");

        HttpResponse<String> response = createNewBook(book);
        Book receivedBook = mapper.readValue(response.body(), Book.class);

        assertEquals(200, response.statusCode());
        assertEquals(book.getAuthor(), receivedBook.getAuthor());
        assertEquals(book.getName(), receivedBook.getName());
        assertNotNull(receivedBook.getId());
    }

    @Test
    @Order(4)
    public void getBookByIdTest() throws IOException, InterruptedException {
        Book book = new Book();
        book.setAuthor("St.King");
        book.setName("It");

        HttpRequest requestPost = HttpRequest.newBuilder()
                .header(NAME_OF_HEADER_CONTEN_TTYPE, VALUE_OF_HEADER_CONTENT_TYPE)
                .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(book)))
                .uri(URI.create(GET_BOOKS))
                .build();

        HttpResponse<String> responsePost = client.send(requestPost, HttpResponse.BodyHandlers.ofString());
        Book receivedBookAfterPostRequest = mapper.readValue(responsePost.body(), Book.class);
        long bookId = receivedBookAfterPostRequest.getId();

        HttpRequest requestGet = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(GET_BOOK_BY_ID.replace("{id}", String.valueOf(bookId))))
                .build();

        HttpResponse<String> response = client.send(requestGet, HttpResponse.BodyHandlers.ofString());

        Book receivedBookAfterGetRequest = mapper.readValue(response.body(), Book.class);

        assertEquals(200, response.statusCode());
        assertEquals(book.getAuthor(), receivedBookAfterGetRequest.getAuthor());
        assertEquals(book.getName(), receivedBookAfterGetRequest.getName());
        assertNotNull(receivedBookAfterGetRequest.getId());
        assertEquals(bookId, receivedBookAfterGetRequest.getId());
    }

    @Test
    @Order(5)
    public void getBookByNonexistentIdTest() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(GET_BOOK_BY_ID.replace("{id}", "20")))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    @Test
    @Order(6)
    public void getBookByWrongIdTest() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .GET().uri(URI.create(GET_BOOK_BY_ID.replace("{id}", "a")))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
    }

    @Test
    @Order(2)
    public void postThreeBooksAndGetAllBooksByIdTest() throws IOException, InterruptedException {
        Book book1 = new Book();
        book1.setAuthor("S.King");
        book1.setName("Green mile");

        Book book2 = new Book();
        book2.setAuthor("S.King");
        book2.setName("It");

        Book book3 = new Book();
        book3.setAuthor("S.King");
        book3.setName("The Finger");

        HttpResponse<String> postResponse1 = createNewBook(book1);
        HttpResponse<String> postResponse2 = createNewBook(book2);
        HttpResponse<String> PostResponse3 = createNewBook(book3);

        HttpRequest getRequest = HttpRequest.newBuilder().GET().uri(URI.create(GET_BOOKS)).build();

        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());

        Book[] receivedBooks = mapper.readValue(getResponse.body(), Book[].class);

        assertEquals(200, getResponse.statusCode());

        assertTrue(receivedBooks.length == 3);
    }
}
