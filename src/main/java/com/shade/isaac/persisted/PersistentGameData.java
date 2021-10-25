package com.shade.isaac.persisted;

import com.shade.isaac.persisted.data.BestiaryCategory;
import com.shade.isaac.persisted.data.CharacterType;
import com.shade.isaac.persisted.data.CompletionMark;
import com.shade.isaac.persisted.data.section.ByteSection;
import com.shade.isaac.persisted.data.section.IntSection;
import com.shade.isaac.persisted.data.section.ObjectSection;
import com.shade.isaac.util.IOUtils;
import com.shade.isaac.util.IsaacCRC;

import java.nio.ByteBuffer;

public class PersistentGameData {
    private final byte[] magic;
    private final int unk1;
    private final int unk2;
    private final ByteSection achievements;
    private final IntSection counters;
    private final IntSection levelCounters;
    private final ByteSection collectibles;
    private final ByteSection miniBosses;
    private final ByteSection bosses;
    private final ByteSection challenges;
    private final IntSection cutscenes;
    private final IntSection gameSettings;
    private final ByteSection specialSeedCounters;
    private final ObjectSection<BestiaryCategory> bestiary;

    public PersistentGameData(ByteBuffer buffer) {
        this.magic = IOUtils.getBytes(buffer, 16);
        this.unk1 = buffer.getInt();

        this.achievements = new ByteSection(buffer);
        this.counters = new IntSection(buffer);
        this.levelCounters = new IntSection(buffer);
        this.collectibles = new ByteSection(buffer);
        this.miniBosses = new ByteSection(buffer);
        this.bosses = new ByteSection(buffer);
        this.challenges = new ByteSection(buffer);
        this.cutscenes = new IntSection(buffer);
        this.gameSettings = new IntSection(buffer);
        this.specialSeedCounters = new ByteSection(buffer);
        this.bestiary = new ObjectSection<>(buffer, x -> new BestiaryCategory[x - 1], BestiaryCategory::new);
        this.unk2 = buffer.getInt();

        final int fileChecksum = buffer.getInt();
        final int realChecksum = checksum(buffer, 16, buffer.limit() - 20);

        if (fileChecksum != realChecksum) {
            throw new IllegalArgumentException(String.format("Invalid checksum: 0x%1$08x (%1$d), expected 0x%2$08x (%2$08d)", fileChecksum, realChecksum));
        }
    }

    public void write(ByteBuffer buffer) {
        buffer.put(magic);
        buffer.putInt(unk1);
        achievements.write(buffer);
        counters.write(buffer);
        levelCounters.write(buffer);
        collectibles.write(buffer);
        miniBosses.write(buffer);
        bosses.write(buffer);
        challenges.write(buffer);
        cutscenes.write(buffer);
        gameSettings.write(buffer);
        specialSeedCounters.write(buffer);
        bestiary.write(buffer);
        buffer.putInt(unk2);
        buffer.putInt(checksum(buffer, 16, buffer.position() - 16));
    }

    public ByteSection getAchievements() {
        return achievements;
    }

    public IntSection getCounters() {
        return counters;
    }

    public IntSection getLevelCounters() {
        return levelCounters;
    }

    public ByteSection getCollectibles() {
        return collectibles;
    }

    public ByteSection getMiniBosses() {
        return miniBosses;
    }

    public ByteSection getBosses() {
        return bosses;
    }

    public ByteSection getChallenges() {
        return challenges;
    }

    public IntSection getCutscenes() {
        return cutscenes;
    }

    public IntSection getGameSettings() {
        return gameSettings;
    }

    public ByteSection getSpecialSeedCounters() {
        return specialSeedCounters;
    }

    public ObjectSection<BestiaryCategory> getBestiary() {
        return bestiary;
    }

    public CompletionMark.State getCompletionMark(CharacterType character, CompletionMark mark) {
        return CompletionMark.State.values()[counters.get()[getMarkIndex(character, mark)]];
    }

    public void setCompletionMark(CharacterType character, CompletionMark mark, CompletionMark.State state) {
        counters.get()[getMarkIndex(character, mark)] = state.ordinal();
    }

    private static int getMarkIndex(CharacterType character, CompletionMark mark) {
        if (character == CharacterType.THE_FORGOTTEN) {
            return switch (mark.getGameType()) {
                case REBIRTH, AFTERBIRTH -> 203 + mark.ordinal();
                case AFTERBIRTH_PLUS -> 213 + (mark.ordinal() - CompletionMark.WRINKLED_PAPER.ordinal());
                case REPENTANCE -> 423 + 34 * (mark.ordinal() - CompletionMark.KNIFE.ordinal()) + character.ordinal();
            };
        } else {
            return switch (character.getGameType()) {
                case REBIRTH, AFTERBIRTH, AFTERBIRTH_PLUS -> switch (mark.getGameType()) {
                    case REBIRTH -> 27 + 14 * mark.ordinal() + character.ordinal();
                    case AFTERBIRTH -> 116 + 14 * (mark.ordinal() - CompletionMark.BRIMSTONE.ordinal()) + character.ordinal();
                    case AFTERBIRTH_PLUS -> 173 + 14 * (mark.ordinal() - CompletionMark.WRINKLED_PAPER.ordinal()) + character.ordinal();
                    case REPENTANCE -> 423 + 34 * (mark.ordinal() - CompletionMark.KNIFE.ordinal()) + character.ordinal();
                };
                case REPENTANCE -> switch (mark.getGameType()) {
                    case REBIRTH, AFTERBIRTH -> 214 + 19 * mark.ordinal() + (character.ordinal() - CharacterType.BETHANY.ordinal());
                    case AFTERBIRTH_PLUS -> 404 + 19 * (mark.ordinal() - CompletionMark.WRINKLED_PAPER.ordinal()) + (character.ordinal() - CharacterType.BETHANY.ordinal());
                    case REPENTANCE -> 423 + 34 * (mark.ordinal() - CompletionMark.KNIFE.ordinal()) + character.ordinal();
                };
            };
        }
    }

    private static int checksum(ByteBuffer buffer, int off, int len) {
        final IsaacCRC crc = new IsaacCRC();
        crc.update(buffer.slice(off, len));
        return (int) crc.getValue();
    }
}
