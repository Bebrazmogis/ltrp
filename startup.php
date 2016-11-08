<?php

require_once("lib/Spyc.php");

$data = spyc_load_file('shoebill/resources.yml');

echo("Using runtimes:\r\n");
foreach($data["runtimes"] as $runtime)
    echo("-".$runtime."\r\n");

$plugins = getJars("shoebill/plugins");
$data["plugins"] = $plugins;
echo("Found ".sizeof($plugins)." plugins\r\n");

$libs = getJars("shoebill/libraries");
echo("Found ".sizeof($libs)." libraries\r\n");

foreach($data['runtimes'] as $runtime)
{
    if(strpos($runtime, "shoebill") !== false)
        array_push($libs, $runtime);
}
$data['runtimes'] = $libs;

$data = spyc_dump($data);
file_put_contents("shoebill/resources.yml", $data);

function getJars($plugin_dir) {
    $files = scandir($plugin_dir);
    $jars = [];
    foreach($files as $file)
    {
        if(endsWith($file, ".jar"))
        {
            array_push($jars, $file);
        }
    }
    return $jars;
}

// copied from stackoverflow
function endsWith($haystack, $needle)
{
    $length = strlen($needle);
    if ($length == 0) {
        return true;
    }

    return (substr($haystack, -$length) === $needle);
}
?>