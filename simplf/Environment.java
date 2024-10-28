package simplf;

class Environment {
    private AssocList values;
    final Environment enclosing;

    Environment() {
        this.values = null;
        this.enclosing = null;
    }

    Environment(Environment enclosing) {
        this.values = null;
        this.enclosing = enclosing;
    }

    Environment(AssocList assocList, Environment enclosing) {
        this.values = assocList;
        this.enclosing = enclosing;
    }

    Environment define(Token varToken, String name, Object value) {
        if (value instanceof SimplfFunction) {
            values = new AssocList(name, value, values);
            return this;
        }
        return new Environment(new AssocList(name, value, values), enclosing);
    }

    void assign(Token name, Object value) {
        AssocList current = values;
        while (current != null) {
            if (current.name.equals(name.lexeme)) {
                current.value = value;
                return;
            }
            current = current.next;
        }

        if (enclosing != null) {
            enclosing.assign(name, value);
            return;
        }

        throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
    }

    Object get(Token name) {
        AssocList current = values;
        while (current != null) {
            if (current.name.equals(name.lexeme)) {
                return current.value;
            }
            current = current.next;
        }

        if (enclosing != null) {
            return enclosing.get(name);
        }

        throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
    }
}