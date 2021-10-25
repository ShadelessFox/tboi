package com.shade.isaac.persisted.data;

public enum CharacterType {
    ISAAC(GameType.REBIRTH),
    MAGDALENE(GameType.REBIRTH),
    CAIN(GameType.REBIRTH),
    JUDAS(GameType.REBIRTH),
    BLUE_BABY(GameType.REBIRTH),
    EVE(GameType.REBIRTH),
    SAMSON(GameType.REBIRTH),
    AZAZEL(GameType.REBIRTH),
    LAZARUS(GameType.REBIRTH),
    EDEN(GameType.REBIRTH),
    THE_LOST(GameType.REBIRTH),
    LILITH(GameType.AFTERBIRTH),
    KEEPER(GameType.AFTERBIRTH),
    APOLLYON(GameType.AFTERBIRTH_PLUS),
    THE_FORGOTTEN(GameType.AFTERBIRTH_PLUS),
    BETHANY(GameType.REPENTANCE),
    JACOB(GameType.REPENTANCE),
    TAINTED_ISAAC(GameType.REPENTANCE),
    TAINTED_MAGDALENE(GameType.REPENTANCE),
    TAINTED_CAIN(GameType.REPENTANCE),
    TAINTED_JUDAS(GameType.REPENTANCE),
    TAINTED_BLUE_BABY(GameType.REPENTANCE),
    TAINTED_EVE(GameType.REPENTANCE),
    TAINTED_SAMSON(GameType.REPENTANCE),
    TAINTED_AZAZEL(GameType.REPENTANCE),
    TAINTED_LAZARUS(GameType.REPENTANCE),
    TAINTED_EDEN(GameType.REPENTANCE),
    TAINTED_LOST(GameType.REPENTANCE),
    TAINTED_LILITH(GameType.REPENTANCE),
    TAINTED_KEEPER(GameType.REPENTANCE),
    TAINTED_APOLLYON(GameType.REPENTANCE),
    TAINTED_FORGOTTEN(GameType.REPENTANCE),
    TAINTED_BETHANY(GameType.REPENTANCE),
    TAINTED_JACOB(GameType.REPENTANCE);

    private final GameType gameType;

    CharacterType(GameType gameType) {
        this.gameType = gameType;
    }

    public GameType getGameType() {
        return gameType;
    }
}
