package com.shade.isaac.persisted.data;

public enum CompletionMark {
    HEART(GameType.REBIRTH),
    CROSS(GameType.REBIRTH),
    INVERTED_CROSS(GameType.REBIRTH),
    STAR(GameType.REBIRTH),
    POLAROID(GameType.REBIRTH),
    NEGATIVE(GameType.REBIRTH),
    BRIMSTONE(GameType.AFTERBIRTH),
    CENT_SIGN(GameType.AFTERBIRTH),
    HUSH_FACE(GameType.AFTERBIRTH),
    WRINKLED_PAPER(GameType.AFTERBIRTH_PLUS),
    KNIFE(GameType.REPENTANCE),
    DAD_NOTE(GameType.REPENTANCE);

    private final GameType gameType;

    CompletionMark(GameType gameType) {
        this.gameType = gameType;
    }

    public GameType getGameType() {
        return gameType;
    }

    public enum State {
        UNEARNED, NORMAL, HARD
    }
}
