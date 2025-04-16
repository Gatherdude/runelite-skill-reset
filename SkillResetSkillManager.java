
package net.runelite.client.plugins.skillreset;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Skill;
import net.runelite.api.events.ExperienceChanged;
import net.runelite.client.eventbus.Subscribe;

import javax.inject.Inject;
import java.util.*;

@Slf4j
public class SkillResetSkillManager
{
    private final Client client;
    private final Map<Skill, Integer> fakeXpMap = new EnumMap<>(Skill.class);
    private final Map<Skill, Integer> fakeLevelMap = new EnumMap<>(Skill.class);
    private final Set<Skill> resetSkills = EnumSet.noneOf(Skill.class);

    @Inject
    private LevelUpAudioPlayer audioPlayer;

    @Inject
    public SkillResetSkillManager(Client client)
    {
        this.client = client;
    }

    public void resetSkill(Skill skill)
    {
        resetSkills.add(skill);
        fakeXpMap.put(skill, 0);
        fakeLevelMap.put(skill, 1);
    }

    public boolean isSkillReset(Skill skill)
    {
        return resetSkills.contains(skill);
    }

    public int getFakeLevel(Skill skill)
    {
        return fakeLevelMap.getOrDefault(skill, client.getRealSkillLevel(skill));
    }

    @Subscribe
    public void onExperienceChanged(ExperienceChanged event)
    {
        Skill skill = event.getSkill();

        if (!resetSkills.contains(skill))
            return;

        int realXp = client.getSkillExperience(skill);
        int previousXp = fakeXpMap.getOrDefault(skill, 0);
        int gainedXp = realXp - previousXp;

        if (gainedXp <= 0)
            return;

        fakeXpMap.put(skill, realXp);
        int previousLevel = fakeLevelMap.getOrDefault(skill, 1);
        int newLevel = getLevelForXp(realXp);

        if (newLevel > previousLevel)
        {
            fakeLevelMap.put(skill, newLevel);
            client.addChatMessage(net.runelite.api.ChatMessageType.GAMEMESSAGE,
                "", "Congratulations! You've advanced a " + skill.getName() + " level to " + newLevel + "!", null);
            audioPlayer.playJingle();
        }
    }

    private int getLevelForXp(int xp)
    {
        for (int level = 1; level <= 99; level++)
        {
            if (xp < xpForLevel(level + 1))
                return level;
        }
        return 99;
    }

    private int xpForLevel(int level)
    {
        int points = 0;
        for (int lvl = 1; lvl < level; lvl++)
            points += Math.floor(lvl + 300.0 * Math.pow(2.0, lvl / 7.0));
        return (int) Math.floor(points / 4);
    }
}
