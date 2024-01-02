package com.fullcycle.admin.catalogo.domain.video;

import com.fullcycle.admin.catalogo.domain.UnitTest;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberID;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.exceptions.DomainException;
import com.fullcycle.admin.catalogo.domain.genre.GenreID;
import com.fullcycle.admin.catalogo.domain.validation.handler.ThrowsValidationHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Year;
import java.util.Set;

public class VideoValidatorTest extends UnitTest {

    @Test
    public void givenNullTitle_whenCallsValidate_shouldReceiveError() {

        final String expectedTitle = null;
        final var expectedDescription = "This is a simple description";
        final var expectedLaunchedAt = Year.of(2022);
        final var expectedDuration = 120.10;
        final var expectedOpened = false;
        final var expectedPublished = false;
        final var expectedRating = Rating.L;
        final var expectedCategories = Set.of(CategoryID.unique());
        final var expectedGenres = Set.of(GenreID.unique());
        final var expectedMembers = Set.of(CastMemberID.unique());

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'title' should not be null";

        final var actualVideo = Video.newVideo(
                null,
                expectedDescription,
                expectedLaunchedAt,
                expectedDuration,
                expectedOpened,
                expectedPublished,
                expectedRating,
                expectedCategories,
                expectedGenres,
                expectedMembers
        );

        final var validator = new VideoValidator(actualVideo, new ThrowsValidationHandler());

        final var actualError = Assertions.assertThrows(DomainException.class, () -> validator.validate());

        Assertions.assertEquals(expectedErrorCount, actualError.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualError.getErrors().get(0).message());

    }

    @Test
    public void givenEmptyTitle_whenCallsValidate_shouldReceiveError() {

        final var expectedTitle = " ";
        final var expectedDescription = "This is a simple description";
        final var expectedLaunchedAt = Year.of(2022);
        final var expectedDuration = 120.10;
        final var expectedOpened = false;
        final var expectedPublished = false;
        final var expectedRating = Rating.L;
        final var expectedCategories = Set.of(CategoryID.unique());
        final var expectedGenres = Set.of(GenreID.unique());
        final var expectedMembers = Set.of(CastMemberID.unique());

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'title' should not be empty";

        final var actualVideo = Video.newVideo(
                expectedTitle,
                expectedDescription,
                expectedLaunchedAt,
                expectedDuration,
                expectedOpened,
                expectedPublished,
                expectedRating,
                expectedCategories,
                expectedGenres,
                expectedMembers
        );

        final var validator = new VideoValidator(actualVideo, new ThrowsValidationHandler());

        final var actualError = Assertions.assertThrows(DomainException.class, () -> validator.validate());

        Assertions.assertEquals(expectedErrorCount, actualError.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualError.getErrors().get(0).message());

    }

    @Test
    public void givenTitleWithLengthGreaterThan255_whenCallsValidate_shouldReceiveError() {

        final var expectedTitle = "Contrary to popular belief, Lorem Ipsum is not simply random text. " +
                "It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old. " +
                "Richard McClintock, a Latin professor at Hampden-Sydney College in Virginia, looked up one of the more obscure " +
                "Latin words, consectetur, from a Lorem Ipsum passage, and going through the cites of the word in classical literature, " +
                "discovered the undoubtable source. Lorem Ipsum comes from sections 1.10.32 and 1.10.33 of \"de Finibus Bonorum " +
                "et Malorum\" (The Extremes of Good and Evil) by Cicero, written in 45 BC. This book is a treatise on the theory of " +
                "ethics, very popular during the Renaissance. The first line of Lorem Ipsum, \"Lorem ipsum dolor sit amet..\", comes " +
                "from a line in section 1.10.32.";
        final var expectedDescription = "This is a simple description";
        final var expectedLaunchedAt = Year.of(2022);
        final var expectedDuration = 120.10;
        final var expectedOpened = false;
        final var expectedPublished = false;
        final var expectedRating = Rating.L;
        final var expectedCategories = Set.of(CategoryID.unique());
        final var expectedGenres = Set.of(GenreID.unique());
        final var expectedMembers = Set.of(CastMemberID.unique());

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'title' must be between and 1 and 255 characters";

        final var actualVideo = Video.newVideo(
                expectedTitle,
                expectedDescription,
                expectedLaunchedAt,
                expectedDuration,
                expectedOpened,
                expectedPublished,
                expectedRating,
                expectedCategories,
                expectedGenres,
                expectedMembers
        );

        final var validator = new VideoValidator(actualVideo, new ThrowsValidationHandler());

        final var actualError = Assertions.assertThrows(DomainException.class, () -> validator.validate());

        Assertions.assertEquals(expectedErrorCount, actualError.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualError.getErrors().get(0).message());

    }

    @Test
    public void givenEmptyDescription_whenCallsValidate_shouldReceiveError() {

        final var expectedTitle = "System Degisn Interviews";
        final var expectedDescription = "";
        final var expectedLaunchedAt = Year.of(2022);
        final var expectedDuration = 120.10;
        final var expectedOpened = false;
        final var expectedPublished = false;
        final var expectedRating = Rating.L;
        final var expectedCategories = Set.of(CategoryID.unique());
        final var expectedGenres = Set.of(GenreID.unique());
        final var expectedMembers = Set.of(CastMemberID.unique());

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'description' should not be empty";

        final var actualVideo = Video.newVideo(
                expectedTitle,
                expectedDescription,
                expectedLaunchedAt,
                expectedDuration,
                expectedOpened,
                expectedPublished,
                expectedRating,
                expectedCategories,
                expectedGenres,
                expectedMembers
        );

        final var validator = new VideoValidator(actualVideo, new ThrowsValidationHandler());

        final var actualError = Assertions.assertThrows(DomainException.class, () -> validator.validate());

        Assertions.assertEquals(expectedErrorCount, actualError.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualError.getErrors().get(0).message());

    }

    @Test
    public void givenDescriptionWithLengthGreaterThan4000_whenCallsValidate_shouldReceiveError() {

        final var expectedTitle = "System Degisn Interviews";
        final var expectedDescription = "Gostaria de enfatizar que a consolidação das estruturas nos obriga à análise do processo de comunicação como um todo. Por outro lado, o entendimento das metas propostas cumpre um papel essencial na formulação das diretrizes de desenvolvimento para o futuro. A certificação de metodologias que nos auxiliam a lidar com a mobilidade dos capitais internacionais auxilia a preparação e a composição das novas proposições. No entanto, não podemos esquecer que a estrutura atual da organização exige a precisão e a definição da gestão inovadora da qual fazemos parte.\n" +
                "\n" +
                "          Não obstante, a competitividade nas transações comerciais garante a contribuição de um grupo importante na determinação do sistema de participação geral. Pensando mais a longo prazo, o consenso sobre a necessidade de qualificação desafia a capacidade de equalização dos conhecimentos estratégicos para atingir a excelência. Nunca é demais lembrar o peso e o significado destes problemas, uma vez que a constante divulgação das informações facilita a criação das condições financeiras e administrativas exigidas.\n" +
                "\n" +
                "          Todavia, a execução dos pontos do programa aponta para a melhoria das condições inegavelmente apropriadas. Percebemos, cada vez mais, que a expansão dos mercados mundiais agrega valor ao estabelecimento dos índices pretendidos. O incentivo ao avanço tecnológico, assim como o início da atividade geral de formação de atitudes é uma das consequências dos níveis de motivação departamental. A prática cotidiana prova que o aumento do diálogo entre os diferentes setores produtivos obstaculiza a apreciação da importância das formas de ação.\n" +
                "\n" +
                "          Todas estas questões, devidamente ponderadas, levantam dúvidas sobre se o julgamento imparcial das eventualidades estende o alcance e a importância dos relacionamentos verticais entre as hierarquias. Podemos já vislumbrar o modo pelo qual o fenômeno da Internet apresenta tendências no sentido de aprovar a manutenção das regras de conduta normativas. Acima de tudo, é fundamental ressaltar que a hegemonia do ambiente político pode nos levar a considerar a reestruturação do sistema de formação de quadros que corresponde às necessidades. O que temos que ter sempre em mente é que a revolução dos costumes prepara-nos para enfrentar situações atípicas decorrentes das posturas dos órgãos dirigentes com relação às suas atribuições.\n" +
                "\n" +
                "          Ainda assim, existem dúvidas a respeito de como o comprometimento entre as equipes maximiza as possibilidades por conta de todos os recursos funcionais envolvidos. Caros amigos, o acompanhamento das preferências de consumo possibilita uma melhor visão global dos métodos utilizados na avaliação de resultados. A nível organizacional, a necessidade de renovação processual afeta positivamente a correta previsão do fluxo de informações.\n" +
                "\n" +
                "          O empenho em analisar a crescente influência da mídia não pode mais se dissociar do impacto na agilidade decisória. As experiências acumuladas demonstram que o surgimento do comércio virtual promove a alavancagem dos procedimentos normalmente adotados. No mundo atual, a consulta aos diversos militantes talvez venha a ressaltar a relatividade dos modos de operação convencionais. É importante questionar o quanto a complexidade dos estudos efetuados oferece uma interessante oportunidade para verificação do levantamento das variáveis envolvidas. Assim mesmo, o novo modelo estrutural aqui preconizado estimula a padronização do retorno esperado a longo prazo.\n" +
                "\n" +
                "          Evidentemente, a determinação clara de objetivos representa uma abertura para a melhoria das diversas correntes de pensamento. Por conseguinte, a adoção de políticas descentralizadoras causa impacto indireto na reavaliação do orçamento setorial. Desta maneira, a valorização de fatores subjetivos faz parte de um processo de gerenciamento dos paradigmas corporativos. Neste sentido, o desenvolvimento contínuo de distintas formas de atuação ainda não demonstrou convincentemente que vai participar na mudança de alternativas às soluções ortodoxas. O cuidado em identificar pontos críticos no desafiador cenário globalizado acarreta um processo de reformulação e modernização do remanejamento dos quadros funcionais.\n" +
                "\n" +
                "          Do mesmo modo, a percepção das dificuldades deve passar por modificações independentemente do investimento em reciclagem técnica. É claro que a contínua expansão de nossa atividade assume importantes posições no estabelecimento das direções preferenciais no sentido do progresso. Por outro lado, o fenômeno da Internet causa impacto indireto na reavaliação do remanejamento dos quadros funcionais.\n" +
                "\n" +
                "          Caros amigos, a contínua expansão de nossa atividade maximiza as possibilidades por conta dos relacionamentos verticais entre as hierarquias. O empenho em analisar a mobilidade dos capitais internacionais garante a contribuição de um grupo importante na determinação do investimento em reciclagem técnica. A nível organizacional, a consulta aos diversos militantes nos obriga à análise das condições inegavelmente apropriadas. Todavia, a valorização de fatores subjetivos auxilia a preparação e a composição do sistema de participação geral. Neste sentido, o consenso sobre a necessidade de qualificação desafia a capacidade de equalização das novas proposições.\n" +
                "\n" +
                "          É claro que o entendimento das metas propostas facilita a criação do sistema de formação de quadros que corresponde às necessidades. Nunca é demais lembrar o peso e o significado destes problemas, uma vez que a execução dos pontos do programa prepara-nos para enfrentar situações atípicas decorrentes dos paradigmas corporativos. No entanto, não podemos esquecer que a expansão dos mercados mundiais afeta positivamente a correta previsão do impacto na agilidade decisória. O incentivo ao avanço tecnológico, assim como a complexidade dos estudos efetuados faz parte de um processo de gerenciamento dos níveis de motivação departamental. A prática cotidiana prova que o aumento do diálogo entre os diferentes setores produtivos representa uma abertura para a melhoria das formas de ação.\n" +
                "\n" +
                "          Gostaria de enfatizar que o julgamento imparcial das eventualidades não pode mais se dissociar das diretrizes de desenvolvimento para o futuro. Não obstante, a crescente influência da mídia apresenta tendências no sentido de aprovar a manutenção das direções preferenciais no sentido do progresso. Por conseguinte, a hegemonia do ambiente político estende o alcance e a importância da gestão inovadora da qual fazemos parte.\n" +
                "\n" +
                "          O que temos que ter sempre em mente é que a adoção de políticas descentralizadoras aponta para a melhoria das posturas dos órgãos dirigentes com relação às suas atribuições. Ainda assim, existem dúvidas a respeito de como a percepção das dificuldades oferece uma interessante oportunidade para verificação dos índices pretendidos. As experiências acumuladas demonstram que o acompanhamento das preferências de consumo possibilita uma melhor visão global dos métodos utilizados na avaliação de resultados.\n" +
                "\n" +
                "          Todas estas questões, devidamente ponderadas, levantam dúvidas sobre se a necessidade de renovação processual acarreta um processo de reformulação e modernização das condições financeiras e administrativas exigidas. A certificação de metodologias que nos auxiliam a lidar com o desenvolvimento contínuo de distintas formas de atuação exige a precisão e a definição das regras de conduta normativas. Podemos já vislumbrar o modo pelo qual a competitividade nas transações comerciais agrega valor ao estabelecimento dos modos de operação convencionais. Pensando mais a longo prazo, a determinação clara de objetivos cumpre um papel essencial na formulação dos conhecimentos estratégicos para atingir a excelência. É importante questionar o quanto o comprometimento entre as equipes é uma das consequências do processo de comunicação como um todo.\n" +
                "\n" +
                "          Assim mesmo, o início da atividade geral de formação de atitudes estimula a padronização do retorno esperado a longo prazo. O cuidado em identificar pontos críticos no novo modelo estrutural aqui preconizado obstaculiza a apreciação da importância das diversas correntes de pensamento. No mundo atual, a estrutura atual da organização ainda não demonstrou convincentemente que vai participar na mudança do orçamento setorial.\n";
        final var expectedLaunchedAt = Year.of(2021);
        final var expectedDuration = 120.10;
        final var expectedOpened = false;
        final var expectedPublished = false;
        final var expectedRating = Rating.L;
        final var expectedCategories = Set.of(CategoryID.unique());
        final var expectedGenres = Set.of(GenreID.unique());
        final var expectedMembers = Set.of(CastMemberID.unique());

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'description' must be between and 1 and 4000 characters";

        final var actualVideo = Video.newVideo(
                expectedTitle,
                expectedDescription,
                expectedLaunchedAt,
                expectedDuration,
                expectedOpened,
                expectedPublished,
                expectedRating,
                expectedCategories,
                expectedGenres,
                expectedMembers
        );

        final var validator = new VideoValidator(actualVideo, new ThrowsValidationHandler());

        final var actualError = Assertions.assertThrows(DomainException.class, () -> validator.validate());

        Assertions.assertEquals(expectedErrorCount, actualError.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualError.getErrors().get(0).message());

    }

    @Test
    public void givenNullLaunchedAt_whenCallsValidate_shouldReceiveError() {

        final var expectedTitle = "System Degisn Interviews";
        final var expectedDescription = "A description";
        final Year expectedLaunchedAt = null;
        final var expectedDuration = 120.10;
        final var expectedOpened = false;
        final var expectedPublished = false;
        final var expectedRating = Rating.L;
        final var expectedCategories = Set.of(CategoryID.unique());
        final var expectedGenres = Set.of(GenreID.unique());
        final var expectedMembers = Set.of(CastMemberID.unique());

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'launchedAt' should not be null";

        final var actualVideo = Video.newVideo(
                expectedTitle,
                expectedDescription,
                null,
                expectedDuration,
                expectedOpened,
                expectedPublished,
                expectedRating,
                expectedCategories,
                expectedGenres,
                expectedMembers
        );

        final var validator = new VideoValidator(actualVideo, new ThrowsValidationHandler());

        final var actualError = Assertions.assertThrows(DomainException.class, () -> validator.validate());

        Assertions.assertEquals(expectedErrorCount, actualError.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualError.getErrors().get(0).message());

    }

    @Test
    public void givenNullRating_whenCallsValidate_shouldReceiveError() {

        final var expectedTitle = "System Degisn Interviews";
        final var expectedDescription = "A description";
        final var expectedLaunchedAt = Year.of(2022);
        final var expectedDuration = 120.10;
        final var expectedOpened = false;
        final var expectedPublished = false;
        final Rating expectedRating = null;
        final var expectedCategories = Set.of(CategoryID.unique());
        final var expectedGenres = Set.of(GenreID.unique());
        final var expectedMembers = Set.of(CastMemberID.unique());

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'rating' should not be null";

        final var actualVideo = Video.newVideo(
                expectedTitle,
                expectedDescription,
                expectedLaunchedAt,
                expectedDuration,
                expectedOpened,
                expectedPublished,
                null,
                expectedCategories,
                expectedGenres,
                expectedMembers
        );

        final var actualError = Assertions.assertThrows(DomainException.class, () -> actualVideo.validate(new ThrowsValidationHandler()));

        Assertions.assertEquals(expectedErrorCount, actualError.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualError.getErrors().get(0).message());

    }

}

