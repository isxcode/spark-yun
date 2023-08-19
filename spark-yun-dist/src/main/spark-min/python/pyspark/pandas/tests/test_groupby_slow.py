#
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

import unittest
from distutils.version import LooseVersion
from itertools import product

import numpy as np
import pandas as pd

from pyspark import pandas as ps
from pyspark.testing.pandasutils import PandasOnSparkTestCase, TestUtils


class GroupBySlowTest(PandasOnSparkTestCase, TestUtils):
    def test_split_apply_combine_on_series(self):
        pdf = pd.DataFrame(
            {
                "a": [1, 2, 6, 4, 4, 6, 4, 3, 7],
                "b": [4, 2, 7, 3, 3, 1, 1, 1, 2],
                "c": [4, 2, 7, 3, None, 1, 1, 1, 2],
                "d": list("abcdefght"),
            },
            index=[0, 1, 3, 5, 6, 8, 9, 9, 9],
        )
        psdf = ps.from_pandas(pdf)

        funcs = [
            ((True, False), ["sum", "min", "max", "count", "first", "last"]),
            ((True, True), ["mean"]),
            ((False, False), ["var", "std", "skew"]),
        ]
        funcs = [(check_exact, almost, f) for (check_exact, almost), fs in funcs for f in fs]

        for as_index in [True, False]:
            if as_index:

                def sort(df):
                    return df.sort_index()

            else:

                def sort(df):
                    return df.sort_values(list(df.columns)).reset_index(drop=True)

            for check_exact, almost, func in funcs:
                for kkey, pkey in [("b", "b"), (psdf.b, pdf.b)]:
                    with self.subTest(as_index=as_index, func=func, key=pkey):
                        if as_index is True or func != "std":
                            self.assert_eq(
                                sort(getattr(psdf.groupby(kkey, as_index=as_index).a, func)()),
                                sort(getattr(pdf.groupby(pkey, as_index=as_index).a, func)()),
                                check_exact=check_exact,
                                almost=almost,
                            )
                            self.assert_eq(
                                sort(getattr(psdf.groupby(kkey, as_index=as_index), func)()),
                                sort(getattr(pdf.groupby(pkey, as_index=as_index), func)()),
                                check_exact=check_exact,
                                almost=almost,
                            )
                        else:
                            # seems like a pandas' bug for as_index=False and func == "std"?
                            self.assert_eq(
                                sort(getattr(psdf.groupby(kkey, as_index=as_index).a, func)()),
                                sort(pdf.groupby(pkey, as_index=True).a.std().reset_index()),
                                check_exact=check_exact,
                                almost=almost,
                            )
                            self.assert_eq(
                                sort(getattr(psdf.groupby(kkey, as_index=as_index), func)()),
                                sort(pdf.groupby(pkey, as_index=True).std().reset_index()),
                                check_exact=check_exact,
                                almost=almost,
                            )

                for kkey, pkey in [(psdf.b + 1, pdf.b + 1), (psdf.copy().b, pdf.copy().b)]:
                    with self.subTest(as_index=as_index, func=func, key=pkey):
                        self.assert_eq(
                            sort(getattr(psdf.groupby(kkey, as_index=as_index).a, func)()),
                            sort(getattr(pdf.groupby(pkey, as_index=as_index).a, func)()),
                            check_exact=check_exact,
                            almost=almost,
                        )
                        self.assert_eq(
                            sort(getattr(psdf.groupby(kkey, as_index=as_index), func)()),
                            sort(getattr(pdf.groupby(pkey, as_index=as_index), func)()),
                            check_exact=check_exact,
                            almost=almost,
                        )

            for check_exact, almost, func in funcs:
                for i in [0, 4, 7]:
                    with self.subTest(as_index=as_index, func=func, i=i):
                        self.assert_eq(
                            sort(getattr(psdf.groupby(psdf.b > i, as_index=as_index).a, func)()),
                            sort(getattr(pdf.groupby(pdf.b > i, as_index=as_index).a, func)()),
                            check_exact=check_exact,
                            almost=almost,
                        )
                        self.assert_eq(
                            sort(getattr(psdf.groupby(psdf.b > i, as_index=as_index), func)()),
                            sort(getattr(pdf.groupby(pdf.b > i, as_index=as_index), func)()),
                            check_exact=check_exact,
                            almost=almost,
                        )

        for check_exact, almost, func in funcs:
            for kkey, pkey in [
                (psdf.b, pdf.b),
                (psdf.b + 1, pdf.b + 1),
                (psdf.copy().b, pdf.copy().b),
                (psdf.b.rename(), pdf.b.rename()),
            ]:
                with self.subTest(func=func, key=pkey):
                    self.assert_eq(
                        getattr(psdf.a.groupby(kkey), func)().sort_index(),
                        getattr(pdf.a.groupby(pkey), func)().sort_index(),
                        check_exact=check_exact,
                        almost=almost,
                    )
                    self.assert_eq(
                        getattr((psdf.a + 1).groupby(kkey), func)().sort_index(),
                        getattr((pdf.a + 1).groupby(pkey), func)().sort_index(),
                        check_exact=check_exact,
                        almost=almost,
                    )
                    self.assert_eq(
                        getattr((psdf.b + 1).groupby(kkey), func)().sort_index(),
                        getattr((pdf.b + 1).groupby(pkey), func)().sort_index(),
                        check_exact=check_exact,
                        almost=almost,
                    )
                    self.assert_eq(
                        getattr(psdf.a.rename().groupby(kkey), func)().sort_index(),
                        getattr(pdf.a.rename().groupby(pkey), func)().sort_index(),
                        check_exact=check_exact,
                        almost=almost,
                    )

    def test_aggregate(self):
        pdf = pd.DataFrame(
            {"A": [1, 1, 2, 2], "B": [1, 2, 3, 4], "C": [0.362, 0.227, 1.267, -0.562]}
        )
        psdf = ps.from_pandas(pdf)

        for as_index in [True, False]:
            if as_index:

                def sort(df):
                    return df.sort_index()

            else:

                def sort(df):
                    return df.sort_values(list(df.columns)).reset_index(drop=True)

            for kkey, pkey in [("A", "A"), (psdf.A, pdf.A)]:
                with self.subTest(as_index=as_index, key=pkey):
                    self.assert_eq(
                        sort(psdf.groupby(kkey, as_index=as_index).agg("sum")),
                        sort(pdf.groupby(pkey, as_index=as_index).agg("sum")),
                    )
                    self.assert_eq(
                        sort(psdf.groupby(kkey, as_index=as_index).agg({"B": "min", "C": "sum"})),
                        sort(pdf.groupby(pkey, as_index=as_index).agg({"B": "min", "C": "sum"})),
                    )
                    self.assert_eq(
                        sort(
                            psdf.groupby(kkey, as_index=as_index).agg(
                                {"B": ["min", "max"], "C": "sum"}
                            )
                        ),
                        sort(
                            pdf.groupby(pkey, as_index=as_index).agg(
                                {"B": ["min", "max"], "C": "sum"}
                            )
                        ),
                    )

                    if as_index:
                        self.assert_eq(
                            sort(psdf.groupby(kkey, as_index=as_index).agg(["sum"])),
                            sort(pdf.groupby(pkey, as_index=as_index).agg(["sum"])),
                        )
                    else:
                        # seems like a pandas' bug for as_index=False and func_or_funcs is list?
                        self.assert_eq(
                            sort(psdf.groupby(kkey, as_index=as_index).agg(["sum"])),
                            sort(pdf.groupby(pkey, as_index=True).agg(["sum"]).reset_index()),
                        )

            for kkey, pkey in [(psdf.A + 1, pdf.A + 1), (psdf.copy().A, pdf.copy().A)]:
                with self.subTest(as_index=as_index, key=pkey):
                    self.assert_eq(
                        sort(psdf.groupby(kkey, as_index=as_index).agg("sum")),
                        sort(pdf.groupby(pkey, as_index=as_index).agg("sum")),
                    )
                    self.assert_eq(
                        sort(psdf.groupby(kkey, as_index=as_index).agg({"B": "min", "C": "sum"})),
                        sort(pdf.groupby(pkey, as_index=as_index).agg({"B": "min", "C": "sum"})),
                    )
                    self.assert_eq(
                        sort(
                            psdf.groupby(kkey, as_index=as_index).agg(
                                {"B": ["min", "max"], "C": "sum"}
                            )
                        ),
                        sort(
                            pdf.groupby(pkey, as_index=as_index).agg(
                                {"B": ["min", "max"], "C": "sum"}
                            )
                        ),
                    )
                    self.assert_eq(
                        sort(psdf.groupby(kkey, as_index=as_index).agg(["sum"])),
                        sort(pdf.groupby(pkey, as_index=as_index).agg(["sum"])),
                    )

        expected_error_message = (
            r"aggs must be a dict mapping from column name to aggregate functions "
            r"\(string or list of strings\)."
        )
        with self.assertRaisesRegex(ValueError, expected_error_message):
            psdf.groupby("A", as_index=as_index).agg(0)

        # multi-index columns
        columns = pd.MultiIndex.from_tuples([(10, "A"), (10, "B"), (20, "C")])
        pdf.columns = columns
        psdf.columns = columns

        for as_index in [True, False]:
            stats_psdf = psdf.groupby((10, "A"), as_index=as_index).agg(
                {(10, "B"): "min", (20, "C"): "sum"}
            )
            stats_pdf = pdf.groupby((10, "A"), as_index=as_index).agg(
                {(10, "B"): "min", (20, "C"): "sum"}
            )
            self.assert_eq(
                stats_psdf.sort_values(by=[(10, "B"), (20, "C")]).reset_index(drop=True),
                stats_pdf.sort_values(by=[(10, "B"), (20, "C")]).reset_index(drop=True),
            )

        stats_psdf = psdf.groupby((10, "A")).agg({(10, "B"): ["min", "max"], (20, "C"): "sum"})
        stats_pdf = pdf.groupby((10, "A")).agg({(10, "B"): ["min", "max"], (20, "C"): "sum"})
        self.assert_eq(
            stats_psdf.sort_values(
                by=[(10, "B", "min"), (10, "B", "max"), (20, "C", "sum")]
            ).reset_index(drop=True),
            stats_pdf.sort_values(
                by=[(10, "B", "min"), (10, "B", "max"), (20, "C", "sum")]
            ).reset_index(drop=True),
        )

        # non-string names
        pdf.columns = [10, 20, 30]
        psdf.columns = [10, 20, 30]

        for as_index in [True, False]:
            stats_psdf = psdf.groupby(10, as_index=as_index).agg({20: "min", 30: "sum"})
            stats_pdf = pdf.groupby(10, as_index=as_index).agg({20: "min", 30: "sum"})
            self.assert_eq(
                stats_psdf.sort_values(by=[20, 30]).reset_index(drop=True),
                stats_pdf.sort_values(by=[20, 30]).reset_index(drop=True),
            )

        stats_psdf = psdf.groupby(10).agg({20: ["min", "max"], 30: "sum"})
        stats_pdf = pdf.groupby(10).agg({20: ["min", "max"], 30: "sum"})
        self.assert_eq(
            stats_psdf.sort_values(by=[(20, "min"), (20, "max"), (30, "sum")]).reset_index(
                drop=True
            ),
            stats_pdf.sort_values(by=[(20, "min"), (20, "max"), (30, "sum")]).reset_index(
                drop=True
            ),
        )

    def test_aggregate_func_str_list(self):
        # this is test for cases where only string or list is assigned
        pdf = pd.DataFrame(
            {
                "kind": ["cat", "dog", "cat", "dog"],
                "height": [9.1, 6.0, 9.5, 34.0],
                "weight": [7.9, 7.5, 9.9, 198.0],
            }
        )
        psdf = ps.from_pandas(pdf)

        agg_funcs = ["max", "min", ["min", "max"]]
        for aggfunc in agg_funcs:

            # Since in Koalas groupby, the order of rows might be different
            # so sort on index to ensure they have same output
            sorted_agg_psdf = psdf.groupby("kind").agg(aggfunc).sort_index()
            sorted_agg_pdf = pdf.groupby("kind").agg(aggfunc).sort_index()
            self.assert_eq(sorted_agg_psdf, sorted_agg_pdf)

        # test on multi index column case
        pdf = pd.DataFrame(
            {"A": [1, 1, 2, 2], "B": [1, 2, 3, 4], "C": [0.362, 0.227, 1.267, -0.562]}
        )
        psdf = ps.from_pandas(pdf)

        columns = pd.MultiIndex.from_tuples([("X", "A"), ("X", "B"), ("Y", "C")])
        pdf.columns = columns
        psdf.columns = columns

        for aggfunc in agg_funcs:
            sorted_agg_psdf = psdf.groupby(("X", "A")).agg(aggfunc).sort_index()
            sorted_agg_pdf = pdf.groupby(("X", "A")).agg(aggfunc).sort_index()
            self.assert_eq(sorted_agg_psdf, sorted_agg_pdf)

    def test_aggregate_relabel(self):
        # this is to test named aggregation in groupby
        pdf = pd.DataFrame({"group": ["a", "a", "b", "b"], "A": [0, 1, 2, 3], "B": [5, 6, 7, 8]})
        psdf = ps.from_pandas(pdf)

        # different agg column, same function
        agg_pdf = pdf.groupby("group").agg(a_max=("A", "max"), b_max=("B", "max")).sort_index()
        agg_psdf = psdf.groupby("group").agg(a_max=("A", "max"), b_max=("B", "max")).sort_index()
        self.assert_eq(agg_pdf, agg_psdf)

        # same agg column, different functions
        agg_pdf = pdf.groupby("group").agg(b_max=("B", "max"), b_min=("B", "min")).sort_index()
        agg_psdf = psdf.groupby("group").agg(b_max=("B", "max"), b_min=("B", "min")).sort_index()
        self.assert_eq(agg_pdf, agg_psdf)

        # test on NamedAgg
        agg_pdf = (
            pdf.groupby("group").agg(b_max=pd.NamedAgg(column="B", aggfunc="max")).sort_index()
        )
        agg_psdf = (
            psdf.groupby("group").agg(b_max=ps.NamedAgg(column="B", aggfunc="max")).sort_index()
        )
        self.assert_eq(agg_psdf, agg_pdf)

        # test on NamedAgg multi columns aggregation
        agg_pdf = (
            pdf.groupby("group")
            .agg(
                b_max=pd.NamedAgg(column="B", aggfunc="max"),
                b_min=pd.NamedAgg(column="B", aggfunc="min"),
            )
            .sort_index()
        )
        agg_psdf = (
            psdf.groupby("group")
            .agg(
                b_max=ps.NamedAgg(column="B", aggfunc="max"),
                b_min=ps.NamedAgg(column="B", aggfunc="min"),
            )
            .sort_index()
        )
        self.assert_eq(agg_psdf, agg_pdf)

    def test_dropna(self):
        pdf = pd.DataFrame(
            {"A": [None, 1, None, 1, 2], "B": [1, 2, 3, None, None], "C": [4, 5, 6, 7, None]}
        )
        psdf = ps.from_pandas(pdf)

        # pd.DataFrame.groupby with dropna parameter is implemented since pandas 1.1.0
        if LooseVersion(pd.__version__) >= LooseVersion("1.1.0"):
            for dropna in [True, False]:
                for as_index in [True, False]:
                    if as_index:

                        def sort(df):
                            return df.sort_index()

                    else:

                        def sort(df):
                            return df.sort_values("A").reset_index(drop=True)

                    self.assert_eq(
                        sort(psdf.groupby("A", as_index=as_index, dropna=dropna).std()),
                        sort(pdf.groupby("A", as_index=as_index, dropna=dropna).std()),
                    )

                    self.assert_eq(
                        sort(psdf.groupby("A", as_index=as_index, dropna=dropna).B.std()),
                        sort(pdf.groupby("A", as_index=as_index, dropna=dropna).B.std()),
                    )
                    self.assert_eq(
                        sort(psdf.groupby("A", as_index=as_index, dropna=dropna)["B"].std()),
                        sort(pdf.groupby("A", as_index=as_index, dropna=dropna)["B"].std()),
                    )

                    self.assert_eq(
                        sort(
                            psdf.groupby("A", as_index=as_index, dropna=dropna).agg(
                                {"B": "min", "C": "std"}
                            )
                        ),
                        sort(
                            pdf.groupby("A", as_index=as_index, dropna=dropna).agg(
                                {"B": "min", "C": "std"}
                            )
                        ),
                    )

            for dropna in [True, False]:
                for as_index in [True, False]:
                    if as_index:

                        def sort(df):
                            return df.sort_index()

                    else:

                        def sort(df):
                            return df.sort_values(["A", "B"]).reset_index(drop=True)

                    self.assert_eq(
                        sort(
                            psdf.groupby(["A", "B"], as_index=as_index, dropna=dropna).agg(
                                {"C": ["min", "std"]}
                            )
                        ),
                        sort(
                            pdf.groupby(["A", "B"], as_index=as_index, dropna=dropna).agg(
                                {"C": ["min", "std"]}
                            )
                        ),
                        almost=True,
                    )

            # multi-index columns
            columns = pd.MultiIndex.from_tuples([("X", "A"), ("X", "B"), ("Y", "C")])
            pdf.columns = columns
            psdf.columns = columns

            for dropna in [True, False]:
                for as_index in [True, False]:
                    if as_index:

                        def sort(df):
                            return df.sort_index()

                    else:

                        def sort(df):
                            return df.sort_values(("X", "A")).reset_index(drop=True)

                    sorted_stats_psdf = sort(
                        psdf.groupby(("X", "A"), as_index=as_index, dropna=dropna).agg(
                            {("X", "B"): "min", ("Y", "C"): "std"}
                        )
                    )
                    sorted_stats_pdf = sort(
                        pdf.groupby(("X", "A"), as_index=as_index, dropna=dropna).agg(
                            {("X", "B"): "min", ("Y", "C"): "std"}
                        )
                    )
                    self.assert_eq(sorted_stats_psdf, sorted_stats_pdf)
        else:
            # Testing dropna=True (pandas default behavior)
            for as_index in [True, False]:
                if as_index:

                    def sort(df):
                        return df.sort_index()

                else:

                    def sort(df):
                        return df.sort_values("A").reset_index(drop=True)

                self.assert_eq(
                    sort(psdf.groupby("A", as_index=as_index, dropna=True)["B"].min()),
                    sort(pdf.groupby("A", as_index=as_index)["B"].min()),
                )

                if as_index:

                    def sort(df):
                        return df.sort_index()

                else:

                    def sort(df):
                        return df.sort_values(["A", "B"]).reset_index(drop=True)

                self.assert_eq(
                    sort(
                        psdf.groupby(["A", "B"], as_index=as_index, dropna=True).agg(
                            {"C": ["min", "std"]}
                        )
                    ),
                    sort(pdf.groupby(["A", "B"], as_index=as_index).agg({"C": ["min", "std"]})),
                    almost=True,
                )

            # Testing dropna=False
            index = pd.Index([1.0, 2.0, np.nan], name="A")
            expected = pd.Series([2.0, np.nan, 1.0], index=index, name="B")
            result = psdf.groupby("A", as_index=True, dropna=False)["B"].min().sort_index()
            self.assert_eq(expected, result)

            expected = pd.DataFrame({"A": [1.0, 2.0, np.nan], "B": [2.0, np.nan, 1.0]})
            result = (
                psdf.groupby("A", as_index=False, dropna=False)["B"]
                .min()
                .sort_values("A")
                .reset_index(drop=True)
            )
            self.assert_eq(expected, result)

            index = pd.MultiIndex.from_tuples(
                [(1.0, 2.0), (1.0, None), (2.0, None), (None, 1.0), (None, 3.0)], names=["A", "B"]
            )
            expected = pd.DataFrame(
                {
                    ("C", "min"): [5.0, 7.0, np.nan, 4.0, 6.0],
                    ("C", "std"): [np.nan, np.nan, np.nan, np.nan, np.nan],
                },
                index=index,
            )
            result = (
                psdf.groupby(["A", "B"], as_index=True, dropna=False)
                .agg({"C": ["min", "std"]})
                .sort_index()
            )
            self.assert_eq(expected, result)

            expected = pd.DataFrame(
                {
                    ("A", ""): [1.0, 1.0, 2.0, np.nan, np.nan],
                    ("B", ""): [2.0, np.nan, np.nan, 1.0, 3.0],
                    ("C", "min"): [5.0, 7.0, np.nan, 4.0, 6.0],
                    ("C", "std"): [np.nan, np.nan, np.nan, np.nan, np.nan],
                }
            )
            result = (
                psdf.groupby(["A", "B"], as_index=False, dropna=False)
                .agg({"C": ["min", "std"]})
                .sort_values(["A", "B"])
                .reset_index(drop=True)
            )
            self.assert_eq(expected, result)

    def test_describe(self):
        # support for numeric type, not support for string type yet
        datas = []
        datas.append({"a": [1, 1, 3], "b": [4, 5, 6], "c": [7, 8, 9]})
        datas.append({"a": [-1, -1, -3], "b": [-4, -5, -6], "c": [-7, -8, -9]})
        datas.append({"a": [0, 0, 0], "b": [0, 0, 0], "c": [0, 8, 0]})
        # it is okay if string type column as a group key
        datas.append({"a": ["a", "a", "c"], "b": [4, 5, 6], "c": [7, 8, 9]})

        percentiles = [0.25, 0.5, 0.75]
        formatted_percentiles = ["25%", "50%", "75%"]
        non_percentile_stats = ["count", "mean", "std", "min", "max"]

        for data in datas:
            pdf = pd.DataFrame(data)
            psdf = ps.from_pandas(pdf)

            describe_pdf = pdf.groupby("a").describe().sort_index()
            describe_psdf = psdf.groupby("a").describe().sort_index()

            # since the result of percentile columns are slightly difference from pandas,
            # we should check them separately: non-percentile columns & percentile columns

            # 1. Check that non-percentile columns are equal.
            agg_cols = [col.name for col in psdf.groupby("a")._agg_columns]
            self.assert_eq(
                describe_psdf.drop(columns=list(product(agg_cols, formatted_percentiles))),
                describe_pdf.drop(columns=formatted_percentiles, level=1),
                check_exact=False,
            )

            # 2. Check that percentile columns are equal.
            # The interpolation argument is yet to be implemented in Koalas.
            quantile_pdf = pdf.groupby("a").quantile(percentiles, interpolation="nearest")
            quantile_pdf = quantile_pdf.unstack(level=1).astype(float)
            self.assert_eq(
                describe_psdf.drop(columns=list(product(agg_cols, non_percentile_stats))),
                quantile_pdf.rename(columns="{:.0%}".format, level=1),
            )

        # not support for string type yet
        datas = []
        datas.append({"a": ["a", "a", "c"], "b": ["d", "e", "f"], "c": ["g", "h", "i"]})
        datas.append({"a": ["a", "a", "c"], "b": [4, 0, 1], "c": ["g", "h", "i"]})
        for data in datas:
            pdf = pd.DataFrame(data)
            psdf = ps.from_pandas(pdf)

            self.assertRaises(
                NotImplementedError, lambda: psdf.groupby("a").describe().sort_index()
            )

        # multi-index columns
        pdf = pd.DataFrame({("x", "a"): [1, 1, 3], ("x", "b"): [4, 5, 6], ("y", "c"): [7, 8, 9]})
        psdf = ps.from_pandas(pdf)

        describe_pdf = pdf.groupby(("x", "a")).describe().sort_index()
        describe_psdf = psdf.groupby(("x", "a")).describe().sort_index()

        # 1. Check that non-percentile columns are equal.
        agg_column_labels = [col._column_label for col in psdf.groupby(("x", "a"))._agg_columns]
        self.assert_eq(
            describe_psdf.drop(
                columns=[
                    tuple(list(label) + [s])
                    for label, s in product(agg_column_labels, formatted_percentiles)
                ]
            ),
            describe_pdf.drop(columns=formatted_percentiles, level=2),
            check_exact=False,
        )

        # 2. Check that percentile columns are equal.
        # The interpolation argument is yet to be implemented in Koalas.
        quantile_pdf = pdf.groupby(("x", "a")).quantile(percentiles, interpolation="nearest")
        quantile_pdf = quantile_pdf.unstack(level=1).astype(float)

        self.assert_eq(
            describe_psdf.drop(
                columns=[
                    tuple(list(label) + [s])
                    for label, s in product(agg_column_labels, non_percentile_stats)
                ]
            ),
            quantile_pdf.rename(columns="{:.0%}".format, level=2),
        )

    def test_aggregate_relabel_multiindex(self):
        pdf = pd.DataFrame({"A": [0, 1, 2, 3], "B": [5, 6, 7, 8], "group": ["a", "a", "b", "b"]})
        pdf.columns = pd.MultiIndex.from_tuples([("y", "A"), ("y", "B"), ("x", "group")])
        psdf = ps.from_pandas(pdf)

        agg_pdf = pdf.groupby(("x", "group")).agg(a_max=(("y", "A"), "max")).sort_index()
        agg_psdf = psdf.groupby(("x", "group")).agg(a_max=(("y", "A"), "max")).sort_index()
        self.assert_eq(agg_pdf, agg_psdf)

        # same column, different methods
        agg_pdf = (
            pdf.groupby(("x", "group"))
            .agg(a_max=(("y", "A"), "max"), a_min=(("y", "A"), "min"))
            .sort_index()
        )
        agg_psdf = (
            psdf.groupby(("x", "group"))
            .agg(a_max=(("y", "A"), "max"), a_min=(("y", "A"), "min"))
            .sort_index()
        )
        self.assert_eq(agg_pdf, agg_psdf)

        # different column, different methods
        agg_pdf = (
            pdf.groupby(("x", "group"))
            .agg(a_max=(("y", "B"), "max"), a_min=(("y", "A"), "min"))
            .sort_index()
        )
        agg_psdf = (
            psdf.groupby(("x", "group"))
            .agg(a_max=(("y", "B"), "max"), a_min=(("y", "A"), "min"))
            .sort_index()
        )
        self.assert_eq(agg_pdf, agg_psdf)

    def test_all_any(self):
        pdf = pd.DataFrame(
            {
                "A": [1, 1, 2, 2, 3, 3, 4, 4, 5, 5],
                "B": [True, True, True, False, False, False, None, True, None, False],
            }
        )
        psdf = ps.from_pandas(pdf)

        for as_index in [True, False]:
            if as_index:

                def sort(df):
                    return df.sort_index()

            else:

                def sort(df):
                    return df.sort_values("A").reset_index(drop=True)

            self.assert_eq(
                sort(psdf.groupby("A", as_index=as_index).all()),
                sort(pdf.groupby("A", as_index=as_index).all()),
            )
            self.assert_eq(
                sort(psdf.groupby("A", as_index=as_index).any()),
                sort(pdf.groupby("A", as_index=as_index).any()),
            )

            self.assert_eq(
                sort(psdf.groupby("A", as_index=as_index).all()).B,
                sort(pdf.groupby("A", as_index=as_index).all()).B,
            )
            self.assert_eq(
                sort(psdf.groupby("A", as_index=as_index).any()).B,
                sort(pdf.groupby("A", as_index=as_index).any()).B,
            )

        self.assert_eq(
            psdf.B.groupby(psdf.A).all().sort_index(), pdf.B.groupby(pdf.A).all().sort_index()
        )
        self.assert_eq(
            psdf.B.groupby(psdf.A).any().sort_index(), pdf.B.groupby(pdf.A).any().sort_index()
        )

        # multi-index columns
        columns = pd.MultiIndex.from_tuples([("X", "A"), ("Y", "B")])
        pdf.columns = columns
        psdf.columns = columns

        for as_index in [True, False]:
            if as_index:

                def sort(df):
                    return df.sort_index()

            else:

                def sort(df):
                    return df.sort_values(("X", "A")).reset_index(drop=True)

            self.assert_eq(
                sort(psdf.groupby(("X", "A"), as_index=as_index).all()),
                sort(pdf.groupby(("X", "A"), as_index=as_index).all()),
            )
            self.assert_eq(
                sort(psdf.groupby(("X", "A"), as_index=as_index).any()),
                sort(pdf.groupby(("X", "A"), as_index=as_index).any()),
            )

        # Test skipna
        pdf = pd.DataFrame({"A": [True, True], "B": [1, np.nan], "C": [True, None]})
        pdf.name = "x"
        psdf = ps.from_pandas(pdf)
        self.assert_eq(
            psdf.groupby("A").all(skipna=False).sort_index(),
            pdf.groupby("A").all(skipna=False).sort_index(),
        )
        self.assert_eq(
            psdf.groupby("A").all(skipna=True).sort_index(),
            pdf.groupby("A").all(skipna=True).sort_index(),
        )

    def test_raises(self):
        psdf = ps.DataFrame(
            {"a": [1, 2, 6, 4, 4, 6, 4, 3, 7], "b": [4, 2, 7, 3, 3, 1, 1, 1, 2]},
            index=[0, 1, 3, 5, 6, 8, 9, 9, 9],
        )
        # test raises with incorrect key
        self.assertRaises(ValueError, lambda: psdf.groupby([]))
        self.assertRaises(KeyError, lambda: psdf.groupby("x"))
        self.assertRaises(KeyError, lambda: psdf.groupby(["a", "x"]))
        self.assertRaises(KeyError, lambda: psdf.groupby("a")["x"])
        self.assertRaises(KeyError, lambda: psdf.groupby("a")["b", "x"])
        self.assertRaises(KeyError, lambda: psdf.groupby("a")[["b", "x"]])

    def test_nunique(self):
        pdf = pd.DataFrame(
            {"a": [1, 1, 1, 1, 1, 0, 0, 0, 0, 0], "b": [2, 2, 2, 3, 3, 4, 4, 5, 5, 5]}
        )
        psdf = ps.from_pandas(pdf)
        self.assert_eq(
            psdf.groupby("a").agg({"b": "nunique"}).sort_index(),
            pdf.groupby("a").agg({"b": "nunique"}).sort_index(),
        )
        if LooseVersion(pd.__version__) < LooseVersion("1.1.0"):
            expected = ps.DataFrame({"b": [2, 2]}, index=pd.Index([0, 1], name="a"))
            self.assert_eq(psdf.groupby("a").nunique().sort_index(), expected)
            self.assert_eq(
                psdf.groupby("a").nunique(dropna=False).sort_index(),
                expected,
            )
        else:
            self.assert_eq(
                psdf.groupby("a").nunique().sort_index(), pdf.groupby("a").nunique().sort_index()
            )
            self.assert_eq(
                psdf.groupby("a").nunique(dropna=False).sort_index(),
                pdf.groupby("a").nunique(dropna=False).sort_index(),
            )
        self.assert_eq(
            psdf.groupby("a")["b"].nunique().sort_index(),
            pdf.groupby("a")["b"].nunique().sort_index(),
        )
        self.assert_eq(
            psdf.groupby("a")["b"].nunique(dropna=False).sort_index(),
            pdf.groupby("a")["b"].nunique(dropna=False).sort_index(),
        )

        nunique_psdf = psdf.groupby("a", as_index=False).agg({"b": "nunique"})
        nunique_pdf = pdf.groupby("a", as_index=False).agg({"b": "nunique"})
        self.assert_eq(
            nunique_psdf.sort_values(["a", "b"]).reset_index(drop=True),
            nunique_pdf.sort_values(["a", "b"]).reset_index(drop=True),
        )

        # multi-index columns
        columns = pd.MultiIndex.from_tuples([("x", "a"), ("y", "b")])
        pdf.columns = columns
        psdf.columns = columns

        if LooseVersion(pd.__version__) < LooseVersion("1.1.0"):
            expected = ps.DataFrame({("y", "b"): [2, 2]}, index=pd.Index([0, 1], name=("x", "a")))
            self.assert_eq(
                psdf.groupby(("x", "a")).nunique().sort_index(),
                expected,
            )
            self.assert_eq(
                psdf.groupby(("x", "a")).nunique(dropna=False).sort_index(),
                expected,
            )
        else:
            self.assert_eq(
                psdf.groupby(("x", "a")).nunique().sort_index(),
                pdf.groupby(("x", "a")).nunique().sort_index(),
            )
            self.assert_eq(
                psdf.groupby(("x", "a")).nunique(dropna=False).sort_index(),
                pdf.groupby(("x", "a")).nunique(dropna=False).sort_index(),
            )

    def test_unique(self):
        for pdf in [
            pd.DataFrame(
                {"a": [1, 1, 1, 1, 1, 0, 0, 0, 0, 0], "b": [2, 2, 2, 3, 3, 4, 4, 5, 5, 5]}
            ),
            pd.DataFrame(
                {
                    "a": [1, 1, 1, 1, 1, 0, 0, 0, 0, 0],
                    "b": ["w", "w", "w", "x", "x", "y", "y", "z", "z", "z"],
                }
            ),
        ]:
            with self.subTest(pdf=pdf):
                psdf = ps.from_pandas(pdf)

                actual = psdf.groupby("a")["b"].unique().sort_index()._to_pandas()
                expect = pdf.groupby("a")["b"].unique().sort_index()
                self.assert_eq(len(actual), len(expect))
                for act, exp in zip(actual, expect):
                    self.assertTrue(sorted(act) == sorted(exp))

    def test_value_counts(self):
        pdf = pd.DataFrame(
            {"A": [np.nan, 2, 2, 3, 3, 3], "B": [1, 1, 2, 3, 3, np.nan]}, columns=["A", "B"]
        )
        psdf = ps.from_pandas(pdf)
        self.assert_eq(
            psdf.groupby("A")["B"].value_counts().sort_index(),
            pdf.groupby("A")["B"].value_counts().sort_index(),
        )
        self.assert_eq(
            psdf.groupby("A")["B"].value_counts(dropna=False).sort_index(),
            pdf.groupby("A")["B"].value_counts(dropna=False).sort_index(),
        )
        self.assert_eq(
            psdf.groupby("A", dropna=False)["B"].value_counts(dropna=False).sort_index(),
            pdf.groupby("A", dropna=False)["B"].value_counts(dropna=False).sort_index(),
            # Returns are the same considering values and types,
            # disable check_exact to pass the assert_eq
            check_exact=False,
        )
        self.assert_eq(
            psdf.groupby("A")["B"].value_counts(sort=True, ascending=False).sort_index(),
            pdf.groupby("A")["B"].value_counts(sort=True, ascending=False).sort_index(),
        )
        self.assert_eq(
            psdf.groupby("A")["B"]
            .value_counts(sort=True, ascending=False, dropna=False)
            .sort_index(),
            pdf.groupby("A")["B"]
            .value_counts(sort=True, ascending=False, dropna=False)
            .sort_index(),
        )
        self.assert_eq(
            psdf.groupby("A")["B"]
            .value_counts(sort=True, ascending=True, dropna=False)
            .sort_index(),
            pdf.groupby("A")["B"]
            .value_counts(sort=True, ascending=True, dropna=False)
            .sort_index(),
        )
        self.assert_eq(
            psdf.B.rename().groupby(psdf.A).value_counts().sort_index(),
            pdf.B.rename().groupby(pdf.A).value_counts().sort_index(),
        )
        self.assert_eq(
            psdf.B.rename().groupby(psdf.A, dropna=False).value_counts().sort_index(),
            pdf.B.rename().groupby(pdf.A, dropna=False).value_counts().sort_index(),
            # Returns are the same considering values and types,
            # disable check_exact to pass the assert_eq
            check_exact=False,
        )
        self.assert_eq(
            psdf.B.groupby(psdf.A.rename()).value_counts().sort_index(),
            pdf.B.groupby(pdf.A.rename()).value_counts().sort_index(),
        )
        self.assert_eq(
            psdf.B.rename().groupby(psdf.A.rename()).value_counts().sort_index(),
            pdf.B.rename().groupby(pdf.A.rename()).value_counts().sort_index(),
        )

    def test_size(self):
        pdf = pd.DataFrame({"A": [1, 2, 2, 3, 3, 3], "B": [1, 1, 2, 3, 3, 3]})
        psdf = ps.from_pandas(pdf)
        self.assert_eq(psdf.groupby("A").size().sort_index(), pdf.groupby("A").size().sort_index())
        self.assert_eq(
            psdf.groupby("A")["B"].size().sort_index(), pdf.groupby("A")["B"].size().sort_index()
        )
        self.assert_eq(
            psdf.groupby("A")[["B"]].size().sort_index(),
            pdf.groupby("A")[["B"]].size().sort_index(),
        )
        self.assert_eq(
            psdf.groupby(["A", "B"]).size().sort_index(),
            pdf.groupby(["A", "B"]).size().sort_index(),
        )

        # multi-index columns
        columns = pd.MultiIndex.from_tuples([("X", "A"), ("Y", "B")])
        pdf.columns = columns
        psdf.columns = columns

        self.assert_eq(
            psdf.groupby(("X", "A")).size().sort_index(),
            pdf.groupby(("X", "A")).size().sort_index(),
        )
        self.assert_eq(
            psdf.groupby([("X", "A"), ("Y", "B")]).size().sort_index(),
            pdf.groupby([("X", "A"), ("Y", "B")]).size().sort_index(),
        )

    def test_diff(self):
        pdf = pd.DataFrame(
            {
                "a": [1, 2, 3, 4, 5, 6] * 3,
                "b": [1, 1, 2, 3, 5, 8] * 3,
                "c": [1, 4, 9, 16, 25, 36] * 3,
            }
        )
        psdf = ps.from_pandas(pdf)

        self.assert_eq(psdf.groupby("b").diff().sort_index(), pdf.groupby("b").diff().sort_index())
        self.assert_eq(
            psdf.groupby(["a", "b"]).diff().sort_index(),
            pdf.groupby(["a", "b"]).diff().sort_index(),
        )
        self.assert_eq(
            psdf.groupby(["b"])["a"].diff().sort_index(),
            pdf.groupby(["b"])["a"].diff().sort_index(),
        )
        self.assert_eq(
            psdf.groupby(["b"])[["a", "b"]].diff().sort_index(),
            pdf.groupby(["b"])[["a", "b"]].diff().sort_index(),
        )
        self.assert_eq(
            psdf.groupby(psdf.b // 5).diff().sort_index(),
            pdf.groupby(pdf.b // 5).diff().sort_index(),
        )
        self.assert_eq(
            psdf.groupby(psdf.b // 5)["a"].diff().sort_index(),
            pdf.groupby(pdf.b // 5)["a"].diff().sort_index(),
        )

        self.assert_eq(psdf.groupby("b").diff().sum(), pdf.groupby("b").diff().sum().astype(int))
        self.assert_eq(psdf.groupby(["b"])["a"].diff().sum(), pdf.groupby(["b"])["a"].diff().sum())

        # multi-index columns
        columns = pd.MultiIndex.from_tuples([("x", "a"), ("x", "b"), ("y", "c")])
        pdf.columns = columns
        psdf.columns = columns

        self.assert_eq(
            psdf.groupby(("x", "b")).diff().sort_index(),
            pdf.groupby(("x", "b")).diff().sort_index(),
        )
        self.assert_eq(
            psdf.groupby([("x", "a"), ("x", "b")]).diff().sort_index(),
            pdf.groupby([("x", "a"), ("x", "b")]).diff().sort_index(),
        )

    def test_rank(self):
        pdf = pd.DataFrame(
            {
                "a": [1, 2, 3, 4, 5, 6] * 3,
                "b": [1, 1, 2, 3, 5, 8] * 3,
                "c": [1, 4, 9, 16, 25, 36] * 3,
            },
            index=np.random.rand(6 * 3),
        )
        psdf = ps.from_pandas(pdf)

        self.assert_eq(psdf.groupby("b").rank().sort_index(), pdf.groupby("b").rank().sort_index())
        self.assert_eq(
            psdf.groupby(["a", "b"]).rank().sort_index(),
            pdf.groupby(["a", "b"]).rank().sort_index(),
        )
        self.assert_eq(
            psdf.groupby(["b"])["a"].rank().sort_index(),
            pdf.groupby(["b"])["a"].rank().sort_index(),
        )
        self.assert_eq(
            psdf.groupby(["b"])[["a", "c"]].rank().sort_index(),
            pdf.groupby(["b"])[["a", "c"]].rank().sort_index(),
        )
        self.assert_eq(
            psdf.groupby(psdf.b // 5).rank().sort_index(),
            pdf.groupby(pdf.b // 5).rank().sort_index(),
        )
        self.assert_eq(
            psdf.groupby(psdf.b // 5)["a"].rank().sort_index(),
            pdf.groupby(pdf.b // 5)["a"].rank().sort_index(),
        )

        self.assert_eq(psdf.groupby("b").rank().sum(), pdf.groupby("b").rank().sum())
        self.assert_eq(psdf.groupby(["b"])["a"].rank().sum(), pdf.groupby(["b"])["a"].rank().sum())

        # multi-index columns
        columns = pd.MultiIndex.from_tuples([("x", "a"), ("x", "b"), ("y", "c")])
        pdf.columns = columns
        psdf.columns = columns

        self.assert_eq(
            psdf.groupby(("x", "b")).rank().sort_index(),
            pdf.groupby(("x", "b")).rank().sort_index(),
        )
        self.assert_eq(
            psdf.groupby([("x", "a"), ("x", "b")]).rank().sort_index(),
            pdf.groupby([("x", "a"), ("x", "b")]).rank().sort_index(),
        )


if __name__ == "__main__":
    from pyspark.pandas.tests.test_groupby_slow import *  # noqa: F401

    try:
        import xmlrunner

        testRunner = xmlrunner.XMLTestRunner(output="target/test-reports", verbosity=2)
    except ImportError:
        testRunner = None
    unittest.main(testRunner=testRunner, verbosity=2)
