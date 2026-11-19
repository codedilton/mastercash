package com.mastercash.mastercash.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.mastercash.mastercash.domain.MovimentacaoDomain
import com.mastercash.mastercash.ui.viewmodel.MovimentacaoViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mastercash.mastercash.utils.FormatUtils
import android.graphics.Color as AndroidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import android.widget.LinearLayout
import android.view.ViewGroup
import android.view.View

@Composable
fun GraficosScreen(navController: androidx.compose.navigation.NavHostController, modifier: Modifier = Modifier) {
    val viewModel: MovimentacaoViewModel = hiltViewModel()

    val movimentacoes by viewModel.movimentacoes
    val totalEntradas by viewModel.totalEntradas
    val totalSaidas by viewModel.totalSaidas
    val saldo by viewModel.saldo
    val categoriaMaiorGasto by viewModel.categoriaMaiorGasto

    var periodoGrafico by remember { mutableStateOf("mensal") }

    val context = LocalContext.current
    val viewFactory = LocalView.current

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = "Gráficos", style = MaterialTheme.typography.headlineMedium) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Voltar",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Cards de resumo
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ResumoCard("Entradas", FormatUtils.formatCurrency(totalEntradas ?: 0.0), Color(0xFF22C55E))
                ResumoCard("Saídas", FormatUtils.formatCurrency(totalSaidas ?: 0.0), Color(0xFFEF4444))
                ResumoCard("Saldo", FormatUtils.formatCurrency(saldo ?: 0.0), if ((saldo ?: 0.0) >= 0) Color(0xFF16A34A) else Color(0xFFDC2626))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Gráfico de Evolução
            Text(
                text = "Evolução de Entradas e Saídas",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = androidx.compose.foundation.layout.Alignment.CenterVertically
            ) {
                Text(
                    text = "Período:",
                    style = MaterialTheme.typography.bodyMedium
                )
                SegmentedButton(
                    selectedValues = setOf(periodoGrafico),
                    onValueChange = { selection ->
                        periodoGrafico = selection.firstOrNull() ?: "mensal"
                    }
                ) {
                    SegmentedButtonOption(value = "semanal", label = { Text(text = "Semanal") })
                    SegmentedButtonOption(value = "mensal", label = { Text(text = "Mensal") })
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            AndroidChartView(
                movimentacoes = movimentacoes ?: emptyList(),
                periodo = periodoGrafico,
                modifier = Modifier.fillMaxWidth().height(300.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Gráficos de rosca por categoria
            Text(
                text = "Distribuição por Categoria",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AndroidPieChartView(
                    title = "Entradas",
                    movimentacoes = movimentacoes ?: emptyList(),
                    tipo = "entrada",
                    modifier = Modifier.weight(1f).height(260.dp)
                )
                AndroidPieChartView(
                    title = "Saídas",
                    movimentacoes = movimentacoes ?: emptyList(),
                    tipo = "saida",
                    modifier = Modifier.weight(1f).height(260.dp)
                )
            }
        }
    }
}

@Composable
private fun ResumoCard(titulo: String, valor: String, cor: Color) {
    Card(
        modifier = Modifier.weight(1f),
        elevation = 2.dp,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
        ) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = valor,
                style = MaterialTheme.typography.bodyLarge,
                color = cor,
                fontWeight = androidx.compose.ui.text.FontWeight.Bold
            )
        }
    }
}

@Composable
fun AndroidChartView(
    movimentacoes: List<MovimentacaoDomain>,
    periodo: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    AndroidView(
        modifier = modifier,
        factory = { context ->
            BarChart(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                description.isEnabled = false
                legend.isEnabled = true
                setTouchEnabled(true)
                setDragEnabled(true)
                setScaleEnabled(false)
                setPinchZoom(false)
            }
        },
        update = { chart ->
            chart.clear()
            val grupos = agruparPorPeriodo(movimentacoes, periodo)

            val entradasEntries = mutableListOf<BarEntry>()
            val saidasEntries = mutableListOf<BarEntry>()
            val labels = mutableListOf<String>()

            grupos.forEachIndexed { index, grupo ->
                labels.add(grupo.label)
                entradasEntries.add(BarEntry(index.toFloat(), grupo.entrada.toFloat()))
                saidasEntries.add(BarEntry(index.toFloat(), grupo.saida.toFloat()))
            }

            val dataSetEntradas = BarDataSet(entradasEntries, "Entradas")
            dataSetEntradas.color = AndroidColor.parseColor("#22C55E")
            dataSetEntradas.valueTextColor = AndroidColor.parseColor("#22C55E")

            val dataSetSaidas = BarDataSet(saidasEntries, "Saídas")
            dataSetSaidas.color = AndroidColor.parseColor("#EF4444")
            dataSetSaidas.valueTextColor = AndroidColor.parseColor("#EF4444")

            val barData = BarData(dataSetEntradas, dataSetSaidas)
            barData.barWidth = 0.35f
            chart.data = barData

            chart.xAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    val index = value.toInt()
                    return if (index >= 0 && index < labels.size) labels[index] else ""
                }
            }
            chart.xAxis.granularity = 1f
            chart.xAxis.position = XAxis.XAxisPosition.BOTTOM

            val leftAxis = chart.axisLeft
            leftAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return FormatUtils.formatCurrency(value.toDouble())
                }
            }
            leftAxis.axisMinimum = 0f

            chart.axisRight.isEnabled = false
            chart.invalidate()
        }
    )
}

@Composable
fun AndroidPieChartView(
    title: String,
    movimentacoes: List<MovimentacaoDomain>,
    tipo: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Column(
        modifier = modifier
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = if (tipo == "entrada") Color(0xFF22C55E) else Color(0xFFEF4444),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        AndroidView(
            modifier = Modifier.fillMaxWidth().height(200.dp),
            factory = { context ->
                PieChart(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    description.isEnabled = false
                    legend.isEnabled = true
                    setUsePercentValues(true)
                    setDrawEntryLabels(true)
                    setHoleRadius(40f)
                    setTransparentCircleRadius(40f)
                }
            },
            update = { chart ->
                chart.clear()
                val valoresPorCategoria = mutableMapOf<String, Double>()
                movimentacoes.forEach { m ->
                    if (m.tipo == tipo) {
                        valoresPorCategoria[m.categoria] = (valoresPorCategoria[m.categoria] ?: 0.0) + m.valor
                    }
                }

                val entries = mutableListOf<PieEntry>()
                val colors = mutableListOf<Int>()
                val cores = listOf(
                    AndroidColor.parseColor("#3B82F6"),
                    AndroidColor.parseColor("#22C55E"),
                    AndroidColor.parseColor("#F59E0B"),
                    AndroidColor.parseColor("#8B5CF6"),
                    AndroidColor.parseColor("#EC4899"),
                    AndroidColor.parseColor("#14B8A6"),
                    AndroidColor.parseColor("#F97316"),
                    AndroidColor.parseColor("#64748B")
                )

                var index = 0
                valoresPorCategoria.forEach { (cat, valor) ->
                    entries.add(PieEntry(valor.toFloat(), cat))
                    colors.add(cores[index % cores.size])
                    index++
                }

                val dataSet = PieDataSet(entries, title)
                dataSet.colors = colors
                dataSet.valueTextSize = 10f
                dataSet.valueTextColor = AndroidColor.BLACK

                val pieData = PieData(dataSet)
                chart.data = pieData
                chart.invalidate()
            }
        )
        if (movimentacoes.none { it.tipo == tipo }) {
            Text(
                text = if (tipo == "entrada") "Sem entradas" else "Sem saídas",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.align(androidx.compose.ui.Alignment.CenterHorizontally)
            )
        }
    }
}

private data class GrupoPeriodo(val label: String, val entrada: Double, val saida: Double)

private fun agruparPorPeriodo(movimentacoes: List<MovimentacaoDomain>, periodo: String): List<GrupoPeriodo> {
    val grupos = mutableMapOf<String, GrupoPeriodo>()
    val ordenado = mutableListOf<String>()

    movimentacoes.forEach { m ->
        val key: String
        val label: String

        if (periodo == "semanal") {
            // Simplificado: usar dia da semana
            val partes = m.data.split("-")
            val dia = partes[2].toInt()
            val mes = partes[1].toInt()
            val ano = partes[0].toInt()
            key = "$ano-${String.format("%02d", mes)}"
            label = "${String.format("%02d", dia)}/${String.format("%02d", mes)}"
        } else {
            val partes = m.data.split("-")
            key = "${partes[0]}-${partes[1]}"
            label = "${partes[1]}/${partes[0]}"
        }

        if (!grupos.containsKey(key)) {
            grupos[key] = GrupoPeriodo(label, 0.0, 0.0)
            ordenado.add(key)
        }
        val grupoAtual = grupos[key]!!
        val novoGrupo = GrupoPeriodo(
            grupoAtual.label,
            if (m.tipo == "entrada") grupoAtual.entrada + m.valor else grupoAtual.entrada,
            if (m.tipo == "saida") grupoAtual.saida + m.valor else grupoAtual.saida
        )
        grupos[key] = novoGrupo
    }

    return ordenado.map { grupos[it]!! }
}
