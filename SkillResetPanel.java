
package net.runelite.client.plugins.skillreset;

import net.runelite.api.Client;
import net.runelite.api.Skill;
import net.runelite.client.ui.PluginPanel;

import javax.swing.*;
import java.awt.*;

public class SkillResetPanel extends PluginPanel
{
    private final SkillResetPlugin plugin;
    private final Client client;
    private final SkillResetSkillManager skillManager;

    public SkillResetPanel(SkillResetPlugin plugin, Client client, SkillResetSkillManager skillManager)
    {
        this.plugin = plugin;
        this.client = client;
        this.skillManager = skillManager;

        setLayout(new GridLayout(0, 1));
        buildPanel();
    }

    private void buildPanel()
    {
        for (Skill skill : Skill.values())
        {
            if (skill == Skill.OVERALL)
                continue;

            JPanel row = new JPanel(new BorderLayout());

            JLabel label = new JLabel(skill.getName());
            row.add(label, BorderLayout.WEST);

            JButton resetButton = new JButton("Reset");
            resetButton.addActionListener(e ->
            {
                skillManager.resetSkill(skill);
                JOptionPane.showMessageDialog(this, skill.getName() + " has been visually reset to level 1!");
            });
            row.add(resetButton, BorderLayout.EAST);

            add(row);
        }
    }
}
