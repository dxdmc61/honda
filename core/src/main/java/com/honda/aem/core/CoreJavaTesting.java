package com.honda.aem.core;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class CoreJavaTesting {

    public static void main(String[] args) {

        List<String> titles = List.of("alice", "bob", "eve");
        List<String> caps = titles.stream()
                .map(s -> s.toUpperCase()) // ALICE, BOB, EVE
                .collect(Collectors.toList());
        System.out.println(caps);

    }

}
