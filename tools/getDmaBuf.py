# -*- coding: utf-8 -*-
# https://source.android.com/devices/graphics/arch-bq-gralloc
import os
import sys
import webbrowser
from pathlib import Path

start = "<html><head></head><body><pre>"
dmaInfo = os.popen("adb shell dmabuf_dump -a").read()
end = "</pre></body></html>"
fo = open("dmaInfo.html", "w+")
fo.write(start + dmaInfo + end)
fo.close
webbrowser.open("dmaInfo.html")
