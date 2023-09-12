### formerly
统计当前文件夹大小
du -h --max-depth=1 work/testing

/usr/bin/env: ‘python’: No such file or directory
https://blog.csdn.net/Master724/article/details/109388653

input correct password but su root failure
https://blog.csdn.net/lvoelife/article/details/81262911

ctrl alt f6 enter terminal
starx enter xdesk

### dns setting
202.106.0.20, 8.8.8.8, 8.8.8.4, 119.29.29.29, 223.5.5.5

### set twenty start up on ubuntu
twenty.py 
try to add sh to /etc/profile,will stay black
finally add desktop icon
https://blog.51cto.com/jiangzhi2013/1293877
https://blog.csdn.net/jianming21/article/details/89716245
https://forum.ubuntu.org.cn/viewtopic.php?t=491025
https://www.cnblogs.com/toSeeMyDream/p/8477662.html

### hotekey set (final only change ctrl and caps)
https://blog.csdn.net/charles_neil/article/details/85094702
https://blog.csdn.net/huangdecai2/article/details/83024936

use wine + autohotkey   /try failure,does't rule
https://wiki.ubuntu.org.cn/Wine
https://www.winehq.org/
https://zhuanlan.zhihu.com/p/409769098
get the ubuntu version

https://www.autohotkey.com/

  103  cat /proc/version
  104  sudo dpkg --add-architecture i386 
  105  wget -nc https://dl.winehq.org/wine-builds/winehq.key
  106  ls
  107  sudo apt-key add winehq.key
  108  sudo add-apt-repository 'deb https://dl.winehq.org/wine-builds/ubuntu/ focal main'
  109  sudo apt update
  110  sudo apt install --install-recommends winehq-stable

https://www.nblogs.com/panjh/articles/13474169.html

https://blog.csdn.net/justitia00/article/details/103945803?utm_medium=distribute.pc_relevant.none-task-blog-2%7Edefault%7ECTRLIST%7Edefault-1.no_search_link&depth_1-utm_source=distribute.pc_relevant.none-task-blog-2%7Edefault%7ECTRLIST%7Edefault-1.no_search_link

### git push test
Beginning August 13, 2021, we will no longer accept account passwords when authenticating Git operations on GitHub.com.

https://docs.github.com/en/authentication/connecting-to-github-with-ssh/testing-your-ssh-connection

### draw.io
https://github.com/jgraph/drawio-desktop/releases/tag/v15.4.0

### plantuml
https://plantuml.com/zh/

### how to uninstall wine

https://askubuntu.com/questions/15551/how-to-remove-wine-completely

```
sudo apt-get remove --auto-remove winehq-stable 
rm -rf $HOME/.wine
rm -f $HOME/.config/menus/applications-merged/*wine*
rm -rf $HOME/.local/share/applications/wine
rm -f $HOME/.local/share/desktop-directories/*wine*
rm -f $HOME/.local/share/icons/*wine*
```

### install pinta (for image edit)

https://launchpad.net/~pinta-maintainers/+archive/ubuntu/pinta-stable

sudo apt-get install pinta



### hotekey set  by autokey

sudo apt-get install autokey-gtk

https://blog.csdn.net/dongfang12n/article/details/99354975

https://www.thinbug.com/q/45948143

https://wiki.archlinux.org/title/Xmodmap



### flameshot (for replace fastCapture stone)

snap install flameshot or sudo apt-get install flameshot

snap remove flameshot

https://www.cnblogs.com/wkfvawl/p/11193837.html

https://github.com/flameshot-org/flameshot/releases/tag/v0.10.2



### timeshift (for system bak)

https://github.com/teejee2008/timeshift



### list by time

https://www.cnblogs.com/pipiyan/p/10600058.html

### for root editor use chinese(studio不能添加在最后)

http://t.zoukankan.com/jiangfeilong-p-11148516.html



### add bazel for root

https://blog.csdn.net/Guo_Python/article/details/116171093
