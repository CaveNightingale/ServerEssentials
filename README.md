# SeverEssentials
该模组提供开Fabric服所需的常用功能，需要[luckperms](https://modrinth.com/mod/luckperms)作为前置
## 命令
### home系列命令
* /home 返回设置出生点的位置（或参数所指定的传送点）
* /sethome 设置一个家
* /back 返回死亡位置
* /delhome 删除一个家
### tpa系列命令
* /tpa 请求传送到别人那里
* /tpahere 请求别人传送到你这里
* /tpaccept 接受传送请求
* /tpdeny 拒绝传送请求
### warp系列命令
* /warp 传送到传送点
* /setwarp 设置传送点
* /delwarp 删除传送点
### essperm系列命令
* /essperm command 为一个命令指派一个权限节点（下级命令需有上级命令权限才能使用）
* /essperm usermod 修改一个管理员的信息（管理级别、是否无视玩家人数上限）
### 杂项命令
* /hat 互换手中物品与头盔物品栏
* /fly 开启飞行模式（仅生存与冒险模式）
* /afk 告诉他人你正在挂机
* /ping 显示延迟
* /sit 坐地上
### serveressentials系列命令
* /serveressentials reload 重新加载配置文件（命令权限除外）
* /serveressentials update 将配置文件更新到最新版本的格式
## 其他
### mobGriefing拆分
* 恶魂、苦力怕和末影人的方块破坏被拆分成单独的游戏规则（ghastGriefing、creeperGriefing和endermanGriefing）
* 村民、凋灵、末影龙游戏规则保持不变，仍为mobGriefing
### 坐下
* 右键可坐下的方块（例如半砖、楼梯可坐下）
* 使用/sit坐地上
* 需要config/Essentials/config中开启