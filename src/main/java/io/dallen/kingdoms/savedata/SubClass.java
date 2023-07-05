package io.dallen.kingdoms.savedata;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class SubClass<T> {
    @Getter @Setter
    private T value;
}
