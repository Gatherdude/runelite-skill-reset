
package net.runelite.client.plugins.skillreset;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;

@Slf4j
@PluginDescriptor(
    name = "Skill Reset Simulator"
)
public class SkillResetPlugin extends Plugin
{
    @Inject
    private Client client;

    @Inject
    private ClientToolbar clientToolbar;

    @Inject
    private EventBus eventBus;

    @Inject
    private SkillResetSkillManager skillManager;

    @Inject
    private SkillResetConfig config;

    private SkillResetPanel panel;
    private NavigationButton navButton;

    @Provides
    SkillResetConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(SkillResetConfig.class);
    }

    @Override
    protected void startUp()
    {
        eventBus.register(skillManager);

        panel = new SkillResetPanel(this, client, skillManager);
        navButton = NavigationButton.builder()
            .tooltip("Skill Reset")
            .icon(null)
            .panel(panel)
            .build();
        clientToolbar.addNavigation(navButton);
    }

    @Override
    protected void shutDown()
    {
        eventBus.unregister(skillManager);
        clientToolbar.removeNavigation(navButton);
    }
}
