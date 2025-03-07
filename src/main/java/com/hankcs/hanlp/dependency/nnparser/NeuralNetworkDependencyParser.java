/*
 * <summary></summary>
 * <author>He Han</author>
 * <email>me@hankcs.com</email>
 * <create-date>2015/11/2 20:54</create-date>
 *
 * <copyright file="NeuralNetworkDependencyParser.java" company="码农场">
 * Copyright (c) 2008-2015, 码农场. All Right Reserved, http://www.hankcs.com/
 * This source is subject to Hankcs. Please contact Hankcs to get more information.
 * </copyright>
 */
package com.hankcs.hanlp.dependency.nnparser;

import com.hankcs.hanlp.corpus.dependency.CoNll.CoNLLSentence;
import com.hankcs.hanlp.corpus.dependency.CoNll.CoNLLWord;
import com.hankcs.hanlp.dependency.AbstractDependencyParser;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.NLPTokenizer;
import com.hankcs.hanlp.dependency.nnparser.util.PosTagUtil;
import com.hankcs.hanlp.tokenizer.StandardTokenizer;

import java.util.ArrayList;
import java.util.List;

/**
 * 基于神经网络分类模型arc-standard转移动作的判决式依存句法分析器
 * @author hankcs
 */
public class NeuralNetworkDependencyParser extends AbstractDependencyParser
{
    /**
     * 内置实例
     */
    private static final AbstractDependencyParser INSTANCE = new NeuralNetworkDependencyParser();
    /**
     * 本Parser使用的分词器，可以自由替换
     */
    public static Segment SEGMENT = NLPTokenizer.SEGMENT;

    @Override
    public CoNLLSentence parse(List<Term> termList)
    {
        List<String> posTagList = PosTagUtil.to863(termList);
        List<String> wordList = new ArrayList<String>(termList.size());
        for (Term term : termList)
        {
            wordList.add(term.word);
        }
        List<Integer> heads = new ArrayList<Integer>(termList.size());
        List<String> deprels = new ArrayList<String>(termList.size());
        parser_dll.parse(wordList, posTagList, heads, deprels);

        CoNLLWord[] wordArray = new CoNLLWord[termList.size()];
        for (int i = 0; i < wordArray.length; ++i)
        {
            wordArray[i] = new CoNLLWord(i + 1, wordList.get(i), posTagList.get(i), termList.get(i).nature.toString());
            wordArray[i].DEPREL = deprels.get(i);
        }
        for (int i = 0; i < wordArray.length; ++i)
        {
            int index = heads.get(i) - 1;
            if (index < 0)
            {
                wordArray[i].HEAD = CoNLLWord.ROOT;
                continue;
            }
            wordArray[i].HEAD = wordArray[index];
        }
        return new CoNLLSentence(wordArray);
    }

    public static CoNLLSentence compute(List<Term> termList)
    {
        return INSTANCE.parse(termList);
    }

    public static CoNLLSentence compute(String text)
    {
        return compute(SEGMENT.seg(text));
    }
}
