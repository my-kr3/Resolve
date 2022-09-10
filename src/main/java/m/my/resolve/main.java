package m.my.resolve;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;


import java.util.List;
import java.util.Random;

public class main extends JavaPlugin implements Listener {

    private int[] butt = {//外层玻璃板
            0,  1,  2,  3,  4,  5,  6,  7,  8,
            9, 10, 11, 12, 13, 14, 15, 16, 17,
            18, 19, 20,            24, 25, 26,
            27, 28, 29,            33, 34, 35,
            36, 37, 38,            42, 43, 44,
            45, 46, 47,            51, 52, 53
    };

    private ItemStack itemStack;
    private ItemStack itemStack1;
    private ItemStack itemStack2;

    @Override
    public void onEnable() {
        seti();
        loadConfig();
        saveDefaultConfig();
        getLogger().info("插件已开启");
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        getLogger().info("关闭");
    }

    @EventHandler
    public void onCilck(InventoryClickEvent e) {
        //获取玩家
        Player player = (Player) e.getWhoClicked();
    }
    //重新加载config文件
    public void loadConfig() {
        reloadConfig();
        seti();
    }

    //设置gui界面内的玻璃板
    public void seti() {

        //1.12版本
        itemStack = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemStack.setItemMeta(itemMeta);

        String itemname = getConfig().getString("确定分解");
        itemStack1 = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);
        ItemMeta itemMeta1 = itemStack1.getItemMeta();
        itemMeta1.setDisplayName(itemname);
        itemStack1.setItemMeta(itemMeta1);

        itemStack2 = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 1);
        ItemMeta itemMeta2 = itemStack2.getItemMeta();
        itemStack2.setItemMeta(itemMeta2);

        /*
         1.16版本API改变

        itemStack = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        ItemMeta itemMeta=itemStack.getItemMeta();
        itemStack.setItemMeta(itemMeta);

        String itemname= getConfig().getString("确定分解");
        itemStack1 = new ItemStack(Material.ORANGE_STAINED_GLASS_PANE);
        ItemMeta itemMeta1 = itemStack1.getItemMeta();
        itemMeta1.setDisplayName(itemname);
        itemStack1.setItemMeta(itemMeta1);

        itemStack2 = new ItemStack(Material.PINK_STAINED_GLASS_PANE);
        ItemMeta itemMeta2=itemStack2.getItemMeta();
        itemStack2.setItemMeta(itemMeta2);
        */

    }

    //指令打开GUI
    public void gui(final Player player) {
        Inventory inv3 = Bukkit.createInventory(null, 9 * 6, getConfig().getString("Title"));

        for (int bb : butt) {
            inv3.setItem(bb, itemStack);
        }
        int [] item = {21,22,23,
                       30,   32,
                       39,40,41,
                       48,   50};
        for (int i=0;i<item.length;i++){

            inv3.setItem(item[i],itemStack2);

        }
        inv3.setItem(49,itemStack1);
        player.closeInventory();
        player.openInventory(inv3);
    }

    //点击GUI反馈
    @EventHandler
    public boolean onClick(InventoryClickEvent e)
    {
        Inventory inv = e.getClickedInventory();
        Player player = (Player) e.getWhoClicked();

        int[] banitem = {//外层玻璃板
                0, 1, 2, 3, 4, 5, 6, 7, 8,
                9, 10, 11, 12, 13, 14, 15, 16, 17,
                18, 19, 20, 21, 22, 23, 24, 25, 26,
                27, 28, 29, 30,     32, 33, 34, 35,
                36, 37, 38, 39, 40, 41, 42, 43, 44,
                45, 46, 47, 48, 49, 50, 51, 52, 53
        };

        if (e.getView().getTitle().equalsIgnoreCase(getConfig().getString("Title"))) {
            //判断哪些格子的物品无法操作
            for (int i = 0; i < banitem.length; i++){
                if (e.getRawSlot() == banitem[i]) {
                    e.setCancelled(true);
                }
            }
            //判断点击哪个格子
            if (e.getRawSlot() == 49) {
                //判断待分解的格子里是否有物品
                try {
                    if (inv.getItem(31).getType() == Material.AIR) {
                    }
                } catch (Exception e1) {
                    player.sendMessage("§c请在空格内放入需要分解的物品再点击确定!");
                    player.closeInventory();
                    return false;
                }
                //判断格子物品是否有lore
                if (inv.getItem(31).getItemMeta().getLore() == null) {
                    player.sendMessage("§c物品不符合分解要求");
                    return false;
                }
                //获取格子内的物品名字
                String itemname = inv.getItem(31).getItemMeta().getDisplayName();
                //获取格子内的物品lore
                List<String> Listlore = inv.getItem(31).getItemMeta().getLore();

                ConfigurationSection List = getConfig().getConfigurationSection("data");
                for (String ss : List.getKeys(false))//获得配置文件第一层
                {
                    Object mm = List.get(ss);
                    ConfigurationSection section = (ConfigurationSection) mm;
                    for (String Configlore : section.getStringList("lore")) {
                        for (String Itemlore : Listlore) {
                            //判断手中物品lore
                            if (Configlore.equalsIgnoreCase(Itemlore)) {
                                //判断手中物品的数量够不够
                                if (inv.getItem(31).getAmount() >= section.getInt("数量")) {
                                    //随机数如果大于文件分解几率
                                    if (getRandom(0, 100) > section.getInt("分解几率")) {
                                        for (String cmd : section.getStringList("成功指令")) {
                                            Bukkit.dispatchCommand((CommandSender) Bukkit.getConsoleSender(), cmd.replaceAll("%player%", player.getName()));
                                        }
                                        //分解成功扣除物品
                                        inv.getItem(31).setAmount(inv.getItem(31).getAmount() - section.getInt("数量"));
                                        return false;
                                    } else {
                                        for (String cmd : section.getStringList("失败指令")) {
                                            Bukkit.dispatchCommand((CommandSender) Bukkit.getConsoleSender(), cmd.replaceAll("%player%", player.getName()));
                                        }
                                        //分解失败扣除物品
                                        inv.getItem(31).setAmount(inv.getItem(31).getAmount() - section.getInt("数量"));
                                        return false;
                                    }
                                } else {
                                    player.sendMessage("手中待分解的物品数量不够");
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    @EventHandler
    public void closgui(InventoryCloseEvent event) {
        Player p = (Player) event.getPlayer();
        if (event.getView().getTitle().equalsIgnoreCase(getConfig().getString("Title"))) {
            ItemStack item = event.getInventory().getItem(31);
            try {
                p.getInventory().addItem(item);
            } catch (Exception e) {
            }
        }
    }

    public boolean onCommand(CommandSender sender, Command command,String label,String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("在游戏中使用!");
            return false;
        }
        Player player = (Player) sender;


        if (label.equalsIgnoreCase("Resolve")) {

            if ("open".equals(args[0])) {//打开gui
                gui(player);
                return true;
            }
            if ("reload".equals(args[0])){
                if (sender.isOp()) {
                    loadConfig();
                    player.sendMessage("数据重载成功");
                }else{
                    sender.sendMessage("您无权限使用该指令");
                }
                return true;
            }
        }
            return true;
    }


    public static String removeColor(String msg)
    {
        return ChatColor.stripColor(msg);
    }

    public static int getRandom(int min, int max) {
        Random random = new Random();
        int s = random.nextInt(max) % (max - min + 1) + min;
        return s;

    }
}

