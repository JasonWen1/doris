// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package org.apache.doris.nereids.trees.expressions.functions.scalar;

import org.apache.doris.catalog.FunctionSignature;
import org.apache.doris.nereids.exceptions.AnalysisException;
import org.apache.doris.nereids.trees.expressions.Expression;
import org.apache.doris.nereids.trees.expressions.functions.PropagateNullable;
import org.apache.doris.nereids.trees.expressions.visitor.ExpressionVisitor;
import org.apache.doris.nereids.types.ArrayType;
import org.apache.doris.nereids.types.BooleanType;

import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * ScalarFunction 'array_exists'.
 */
public class ArrayExists extends ScalarFunction
        implements HighOrderFunction, PropagateNullable {

    public static final List<FunctionSignature> SIGNATURES = ImmutableList.of(
            FunctionSignature.ret(ArrayType.of(BooleanType.INSTANCE)).args(ArrayType.of(BooleanType.INSTANCE))
    );

    private ArrayExists(List<Expression> expressions) {
        super("array_exists", expressions);
    }

    /**
     * constructor with arguments.
     * array_exists(lambda, a1, ...) = array_exists(array_map(lambda, a1, ...))
     */
    public ArrayExists(Expression arg) {
        super("array_exists", new ArrayMap(arg));
        if (!(arg instanceof Lambda)) {
            throw new AnalysisException(
                    String.format("The 1st arg of %s must be lambda but is %s", getName(), arg));
        }
    }

    /**
     * withChildren.
     */
    @Override
    public ArrayExists withChildren(List<Expression> children) {
        return new ArrayExists(children);
    }

    @Override
    public <R, C> R accept(ExpressionVisitor<R, C> visitor, C context) {
        return visitor.visitArrayExists(this, context);
    }

    @Override
    public List<FunctionSignature> getImplSignature() {
        return SIGNATURES;
    }
}
