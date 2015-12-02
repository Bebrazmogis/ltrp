forward AFunction(thing[], &Float:x, &Float:y, &Float:z);
public AFunction(thing[], &Float:x, &Float:y, &Float:z)
{
    x = 1242.0;
    y = 2334.5;
    z = 75.32;
    printf("ltrp.pwn : AFunction returning %f %f %f", x ,y ,z);
}