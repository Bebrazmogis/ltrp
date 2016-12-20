<?php 

$root = scandir(".");
foreach($root as $file)
{
    if(is_dir($file) && strpos($file, ".") === false)
    {
        try {
            $xml = new SimpleXMLElement(file_get_contents($file."/pom.xml"));
        } catch(Exception $e) {
            die($e);
        }
        $groupId = $xml->groupId;
        if($groupId == "")
            $groupId = $xml->parent->groupId;
        echo($groupId.":".$xml->artifactId." in ".$file."\r\n");
    }
}

?>