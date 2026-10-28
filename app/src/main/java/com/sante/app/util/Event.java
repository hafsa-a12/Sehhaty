package com.sante.app.util;

/**
 * Enveloppe pour un message "consommable une seule fois" exposé via LiveData
 * (Toast, navigation) : évite de le rejouer après une rotation d'écran.
 */
public class Event<T> {

    private final T content;
    private boolean handled = false;

    public Event(T content) {
        this.content = content;
    }

    /** @return le contenu la première fois, puis null. */
    public T getContentIfNotHandled() {
        if (handled) return null;
        handled = true;
        return content;
    }

    public T peek() { return content; }
}
