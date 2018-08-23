package org.chris.demo.rx.model;

import java.util.Arrays;
import java.util.List;

import lombok.Data;
import lombok.NonNull;

@Data
public class Player {

    @NonNull
    private String name;

    @NonNull
    private Integer age;

    public static Player RONALDO = new Player("Ronaldo", 33);
    public static Player MESSI = new Player("Messi", 31);
    public static Player MODRIC = new Player("Modric", 33);
    public static Player NEYMAR = new Player("Neymar", 28);
    public static Player MBAPPÉ = new Player("Mbappé", 19);

    public static List<Player> ALL = Arrays.asList(RONALDO, MESSI, MODRIC, NEYMAR, MBAPPÉ);

}
