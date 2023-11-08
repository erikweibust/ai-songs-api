package net.weibust.aisongsapi;

import org.springframework.ai.client.AiClient;
import org.springframework.ai.client.AiResponse;
import org.springframework.ai.parser.BeanOutputParser;
import org.springframework.ai.prompt.Prompt;
import org.springframework.ai.prompt.PromptTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/songs")
public class SongsController {

    private final AiClient aiClient;

    // inject via constructor injection, AiClient
    public SongsController(AiClient aiClient){
        this.aiClient = aiClient;
    }

    // handle simple get request for /topsong
    @GetMapping("/topsong")
    public String topSong() {
        String prompt = "What was the Billboard number one year-end top 100 single for 1980?";

        return aiClient.generate(prompt);
    }
    
    @GetMapping("/topsong/{year}")
    public TopSong topSong(@PathVariable("year") int year) {
        BeanOutputParser<TopSong> parser = new BeanOutputParser<>(TopSong.class);

        String promptString = """
            What was the Billboard number one year-end top 100 single for {year}?
            {format}
            """;

        PromptTemplate template = new PromptTemplate(promptString);
        template.add("year", year);

        // let Spring AI parser ask OpenAI what format it thinks we need
        template.add("format", parser.getFormat());
        template.setOutputParser(parser);

        System.err.println(" FORMAT STRING: " + parser.getFormat());

        Prompt prompt = template.create();
        AiResponse aiResponse = aiClient.generate(prompt);
        String text = aiResponse.getGeneration().getText();

        return parser.parse(text);
    }
    
    /*@GetMapping("/topsong/{year}")
    public String topSong(@PathVariable("year") int year) {
        String prompt =
            "What was the Billboard number one year-end top 100 single for {year}?";

        PromptTemplate template = new PromptTemplate(prompt);
        template.add("year", year);

        return aiClient.generate(template.render());
    }*/
    
}