<?php

require_once("lib/Spyc.php");

$default_repos = <<<EOD
  - id: central
    url: http://repo1.maven.org/maven2/

  - id: gtaun-public-repo
    url: http://repo.gtaun.net/content/groups/public
EOD;

$data = spyc_load_file('shoebill/resources.yml');

echo("Using runtimes:\r\n");
foreach($data["runtimes"] as $runtime)
    echo("-".$runtime."\r\n");

// If no repo data, add default
if(!isset($data['repositories']))
{
    echo("Repositories not found, adding default\r\n");
    $data['repositories'] = $default_repos;
}

// Get plugins
$plugins = getJars("shoebill/plugins");
$data["plugins"] = $plugins;
echo("Found ".sizeof($plugins)." plugins\r\n");

// Get libraries Wait it shouldn't be built there :/
// TODO
$libs = getJars("shoebill/libraries");
echo("Found ".sizeof($libs)." libraries\r\n");
$data['plugins'] = array_merge($libs, $data['plugins']);

$data = Spyc::YAMLDump($data, false, 0, false);
file_put_contents("shoebill/resources.yml", $data);

function getJars($plugin_dir) {
    $files = scandir($plugin_dir);
    $jars = [];
    foreach($files as $file)
    {
        $index = strpos($file, ".jar");
        if($index !== false)
        {
            array_push($jars, substr($file, 0, $index));
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