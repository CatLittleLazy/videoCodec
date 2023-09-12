<?php
header("content-type:text/html;charset=utf-8")
$val = exec('python3 test.py 12 32')
$cmd = shell_exec($val);
echo "a与b的和是： $cmd";
?>