package org.heigit.bigspatialdata.ohsome.ohsomeapi.utils.tagfilter;

import org.heigit.bigspatialdata.oshdb.osm.OSMType;
import org.heigit.bigspatialdata.oshdb.util.tagtranslator.OSMTag;
import org.heigit.bigspatialdata.oshdb.util.tagtranslator.OSMTagInterface;
import org.heigit.bigspatialdata.oshdb.util.tagtranslator.OSMTagKey;
import org.heigit.bigspatialdata.oshdb.util.tagtranslator.TagTranslator;
import org.jparsec.OperatorTable;
import org.jparsec.Parser;
import org.jparsec.Parsers;
import org.jparsec.Scanners;
import org.jparsec.Terminals.StringLiteral;
import org.jparsec.pattern.Patterns;

public class FilterParser {
  private Parser<FilterExpression> parser;

  public FilterParser(TagTranslator tt) {
    final Parser<Void> whitespace = Scanners.WHITESPACES.skipMany();

    final Parser<String> keystr = Patterns.regex("[a-zA-Z][a-zA-Z_@0-9:]*")
        .toScanner("KEY_STRING")
        .source();
    final Parser<String> string = keystr.or(StringLiteral.DOUBLE_QUOTE_TOKENIZER);

    final Parser<String> equals = Patterns.string("=").toScanner("EQUALS")
        .map(ignored -> "=");
    final Parser<String> notEquals = Patterns.string("!=").toScanner("NOT_EQUALS")
        .map(ignored -> "!=");
    final Parser<String> colon = Patterns.string(":").toScanner("COLON")
        .map(ignored -> ":");
    final Parser<String> type = Patterns.string("type").toScanner("TYPE")
        .map(ignored -> "type");
    final Parser<OSMType> node = Patterns.string("node").toScanner("NODE")
        .map(ignored -> OSMType.NODE);
    final Parser<OSMType> way = Patterns.string("way").toScanner("WAY")
        .map(ignored -> OSMType.WAY);
    final Parser<OSMType> relation = Patterns.string("relation").toScanner("RELATION")
        .map(ignored -> OSMType.RELATION);
    final Parser<String> star = Patterns.string("*").toScanner("STAR")
        .map(ignored -> "*");

    final Parser<FilterExpression> tagFilter = Parsers.sequence(
        string,
        whitespace,
        Parsers.or(equals, notEquals),
        whitespace,
        Parsers.or(string, star),
        (key, ignored, selector, ignored2, value) -> {
          OSMTagInterface tag = value.equals("*")
              ? new OSMTagKey(key)
              : new OSMTag(key, value);
          return TagFilter.fromSelector(selector, tag, tt);
        });
    final Parser<FilterExpression> typeFilter = Parsers.sequence(
        type,
        whitespace,
        colon,
        whitespace,
        Parsers.or(node, way, relation))
        .map(TypeFilter::new);

    final Parser<FilterExpression> filter = Parsers.or(tagFilter, typeFilter);

    final Parser<Void> parensStart = Patterns.string("(").toScanner("(");
    final Parser<Void> parensEnd = Patterns.string(")").toScanner(")");
    final Parser.Reference<FilterExpression> ref = Parser.newReference();
    final Parser<FilterExpression> unit = ref.lazy().between(parensStart, parensEnd)
        .or(filter);
    final Parser<String> and = whitespace
        .followedBy(Patterns.string("and").toScanner("AND"))
        .followedBy(whitespace)
        .map(ignored -> "or");
    final Parser<String> or = whitespace
        .followedBy(Patterns.string("or").toScanner("OR"))
        .followedBy(whitespace)
        .map(ignored -> "or");
    final Parser<String> not = whitespace
        .followedBy(Patterns.string("not").toScanner("NOT"))
        .followedBy(whitespace)
        .map(ignored -> "not");
    final Parser<FilterExpression> parser = new OperatorTable<FilterExpression>()
        .infixl(or.retn((a, b) -> BinaryOperator.fromOperator(a, "or", b)), 10)
        .infixl(and.retn((a, b) -> BinaryOperator.fromOperator(a, "and", b)), 20)
        .prefix(not.retn(x -> UnaryOperator.fromOperator("not", x)), 50)
        .build(unit);
    ref.set(parser);
    this.parser = parser;
  }

  public FilterExpression parse(String str) {
    return this.parser.parse(str);
  }
}
