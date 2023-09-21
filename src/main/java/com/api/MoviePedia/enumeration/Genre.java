package com.api.MoviePedia.enumeration;

public enum Genre {
    Action,
    Comedy,
    Drama,
    Fantasy,
    Horror,
    Mystery,
    Romance,
    Thriller,
    Western;

    public static Genre getGenre(String name){
        name = name.toLowerCase();
        return switch (name) {
            case "action" -> Action;
            case "comedy" -> Comedy;
            case "drama" -> Drama;
            case "fantasy" -> Fantasy;
            case "horror" -> Horror;
            case "mystery" -> Mystery;
            case "romance" -> Romance;
            case "thriller" -> Thriller;
            case "western" -> Western;
            default -> null;
        };
    }
}
