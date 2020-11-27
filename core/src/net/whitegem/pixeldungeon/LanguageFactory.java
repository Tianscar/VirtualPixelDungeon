package net.whitegem.pixeldungeon;

import com.badlogic.gdx.Gdx;
import com.watabou.pixeldungeon.sprites.CharSprite;
import com.watabou.pixeldungeon.utils.GLog;
import com.watabou.pixeldungeon.PixelDungeon;

/**
 * Created by Carl-Station on 01/22/15.
 */
public class LanguageFactory
{
    public static final LanguageFactory INSTANCE;

    static
    {
        INSTANCE = new LanguageFactory();
    }

    public StoredFormat stored = new StoredFormat();

    public String language;
    // HashMap字典
    private Translator translator;

    private LanguageFactory()
    {
        language = PixelDungeon.language();
        translator = new Translator(language);
    }
    
    public static String getTranslation(String k){
        return getTranslation(null, k);
    }
    
    public static String getTranslation(Object o, String k){
        return getTranslation(o.getClass(), k);
    }

    public static String getTranslation(Class c, String k)
    {
        String key;
        if (c != null){
            key = c.getName().replace("com.watabou.pixeldungeon", "");
            key += "." + k;
        } else {
			key = k;
            }
            
        if(key != null && !key.equals("")){
        if (!INSTANCE.language.equals("en"))
        {
            System.out.println("嘗試取得翻譯 >>> " + key.toLowerCase());
            String s = null;

            

            if (INSTANCE.stored.contains(key.toLowerCase()))
            {
                s = INSTANCE.stored.get(key.toLowerCase());
            }
            else if (INSTANCE.stored.contains(key.toLowerCase().replaceAll("\\.$", "")))
            {
                s = INSTANCE.stored.get(key.toLowerCase().replaceAll("\\.$", "")) + ".";
            }

            // 都沒有找到
            if (s == null)
            {
                // 嘗試翻譯
                return INSTANCE.translate(key);
            }

            System.out.println("翻譯完成 >>> " + s);
            return s;
        }
        } else if (key == null)
        {
            return null;
        } else {
            return key;
        }
        return key;
    }

    public void setLanguage(String lang)
    {
        language = lang;
        translator = new Translator(language);
    }

    public String translate(String originalText)
    {
        String textTemp = originalText.replace("\n", "\\n").trim().toLowerCase();

        System.out.println("尋找字典 >>> " + textTemp);

        if (translator.hasKey(textTemp))
        {
            System.out.println("KEY存在(1)"+translator.translate(textTemp).replace("\\n", "\n"));
            return translator.translate(textTemp).replace("\\n", "\n");
        }
        // 移除最後的"."字元 進行比對
        if (translator.hasKey(textTemp.replaceAll("\\.$", "")))
        {
            System.out.println("KEY存在(1)"+translator.translate(textTemp.replaceAll("\\.$", "")).replace("\\n", "\n"));
            return translator.translate(textTemp.replaceAll("\\.$", "")).replace("\\n", "\n") + ".";
        }
        System.out.println("KEY不存在");
        return originalText;
    }

    public boolean hasKey(String key)
    {
        key = key.trim().replace("\n", "\\n");
        return translator.hasKey(key) || translator.hasKey(key.replaceAll("\\.$", ""));
    }

    // 只有當格式化文字出現 才會執行到這一行
    public void addFormatTranslation(String formattedText, String format, Object... args)
    {
        Gdx.app.log("LanguageFactory::addFormatTranslation", "Utils.format >>> " + formattedText + " ||| " + format + " ||| " + args);
        for (int i = 0; i < args.length; i++)
        {
            if (args[i] instanceof String)
            {
                if (stored.contains(args[i].toString().toLowerCase()))
                {
                    args[i] = stored.get(args[i].toString().toLowerCase());
                }
                else if (stored.contains(args[i].toString().toLowerCase().replaceAll("\\.$", "")))
                {
                    args[i] = stored.get(args[i].toString().toLowerCase().replaceAll("\\.$", ""));
                }
                else if (hasKey(args[i].toString().trim().toLowerCase()))
                {
                    args[i] = translate(args[i].toString().trim());
                }
            }
        }

        // 如果已經有這個Key
        if (hasKey(format))
        {
            System.out.println("(1)保存到StoredFormat: Key = " + formattedText.toLowerCase() + " Value = " + String.format(translate(format), args));
            stored.put(formattedText.toLowerCase(), String.format(translate(format), args));
        }
        else
        {
            System.out.println("(2)保存到StoredFormat: Key = " + formattedText.toLowerCase() + " Value = " + String.format(format, args));
            stored.put(formattedText.toLowerCase(), String.format(format, args));
        }
    }

    public String[] splitWords(String paragraph)
    {
        return translator.splitWords(paragraph);
    }
}
