package no.hvl.dat250.rest.todos;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.gson.Gson;

import static spark.Spark.*;

/**
 * Rest-Endpoint.
 */
public class TodoAPI {

    private static List<Todo> todos = new ArrayList<>();
    private static long nextId = 1L;
    private static final Gson gson = new Gson();

    public static void main(String[] args) {
        if (args.length > 0) {
            port(Integer.parseInt(args[0]));
        } else {
            port(8080);
        }

        after((req, res) -> res.type("application/json"));

        get("/todos", (req, res) -> {
            List<String> todosJson = new ArrayList<>();
            todos.stream().forEach(todo -> todosJson.add(gson.toJson(todo)));
            return todosJson;
        });

        get("/todos/:id", (req, res) -> {
            long id = -1;
            try {
                id = Long.parseLong(req.params(":id"));
            } catch (NumberFormatException e) {
                return "The id \"" + req.params(":id") + "\" is not a number!";
            }
            Todo matchingTodo = searchTodoById(id);
            if (matchingTodo == null) {
                return "Todo with the id \"" + id + "\" not found!";
            }
            return gson.toJson(matchingTodo);
        });

        post("/todos", (req, res) -> {
            Todo todo = gson.fromJson(req.body(), Todo.class);
            Todo newTodo = new Todo(nextId++, todo.getSummary(), todo.getDescription());
            todos.add(newTodo);
            return gson.toJson(newTodo);
        });

        put("/todos/:id", (req, res) -> {
            long id = -1;
            try {
                id = Long.parseLong(req.params(":id"));
            } catch (NumberFormatException e) {
                return "The id \"" + req.params(":id") + "\" is not a number!";
            }
            Todo matchingTodo = searchTodoById(id);
            if (matchingTodo == null) {
                return "Todo with the id \"" + id + "\" not found!";
            }
            Todo newTodo = gson.fromJson(req.body(), Todo.class);
            long finalId = id;
            todos = todos.stream().map(todo -> todo.getId() != finalId ? todo : newTodo).collect(Collectors.toList());
            return gson.toJson(newTodo);
        });

        delete("/todos/:id", (req, res) -> {
            long id = -1;
            try {
                id = Long.parseLong(req.params(":id"));
            } catch (NumberFormatException e) {
                return "The id \"" + req.params(":id") + "\" is not a number!";
            }
            Todo matchingTodo = searchTodoById(id);
            if (matchingTodo == null) {
                return "Todo with the id \"" + id + "\" not found!";
            }
            Todo todoToBeDeleted = matchingTodo;
            todos.remove(todoToBeDeleted);
            return gson.toJson(todoToBeDeleted);
        });

    }

    private static Todo searchTodoById(long id) {
        Optional<Todo> optionalTodo = todos.stream().filter(todo -> todo.getId() == id).findFirst();
        if (optionalTodo.isPresent()) {
            return optionalTodo.get();
        }
        return null;
    }

}
